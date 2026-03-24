package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateReviewRequest;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import com.logiclab.documentcontrolsystem.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Review createReview(CreateReviewRequest request, User currentUser){
        checkIsReviewerOrAdmin(currentUser);

        DocumentVersion version = documentVersionRepository.findById(request.getDocumentVersionId())
                .orElseThrow(() -> new RuntimeException("Document version not found!"));

        if(reviewRepository.existsByDocumentVersion(version)){
            throw new RuntimeException("This version has already been reviewed!");
        }
        checkIsInReview(version);

        Review review = new Review();
        review.setDocumentVersion(version);
        review.setReviewer(currentUser);
        review.setDecision(request.getReviewDecision());
        approveOrReject(request.getReviewDecision(),version,currentUser);
        review.setComment(request.getComment());
        review.setReviewedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.VERSION_REVIEW,
                savedReview.getId(),
                currentUser + "created review with ID: " + savedReview.getId()
        );

        return savedReview;
    }

    private void checkIsReviewerOrAdmin(User user){
        boolean hasAccess = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.REVIEWER
                        || role.getName() == RoleName.ADMIN);

        if(!hasAccess){
            throw new RuntimeException("You don't have permission to perform this action!");
        }
    }
    private void checkIsInReview(DocumentVersion version){
        if(!(version.getStatus()== VersionStatus.IN_REVIEW))
            throw new RuntimeException("Can't review this version!");

    }
    private void approveOrReject(ReviewDecision reviewDecision, DocumentVersion version,User user){
        if(reviewDecision == ReviewDecision.APPROVED){
            Document document=version.getDocument();

            DocumentVersion currentActiveVersion = document.getActiveVersion();
            currentActiveVersion.setActive(false);
            currentActiveVersion.setStatus(VersionStatus.OLD_VERSION);
            documentVersionRepository.save(currentActiveVersion);

            version.setActive(true);
            version.setStatus(VersionStatus.ACTIVE);
            documentVersionRepository.save(version);

            document.setActiveVersion(version);
            documentRepository.save(document);

            auditLogService.log(
                    user,
                    AuditAction.APPROVE,
                    AuditEntityType.DOCUMENT_VERSION,
                    version.getId(),
                    user.getUsername() + " approved version with ID: " + version.getId()
            );
        }
        else if(reviewDecision==ReviewDecision.REJECTED){
            version.setStatus(VersionStatus.REJECTED);
            version.setActive(false);
            documentVersionRepository.save(version);

            auditLogService.log(
                    user,
                    AuditAction.REJECT,
                    AuditEntityType.DOCUMENT_VERSION,
                    version.getId(),
                    user.getUsername() + " rejected version with ID: " + version.getId()
            );
        }

        else
            throw new RuntimeException("Invalid review decision!");
    }

}
