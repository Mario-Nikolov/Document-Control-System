package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateVersionRequest;
import com.logiclab.documentcontrolsystem.dto.response.DocumentVersionResponse;
import com.logiclab.documentcontrolsystem.exceptions.DocumentNotFoundException;
import com.logiclab.documentcontrolsystem.exceptions.NoActiveVersionFoundException;
import com.logiclab.documentcontrolsystem.exceptions.NoPermissionException;
import com.logiclab.documentcontrolsystem.exceptions.NoVersionsException;
import com.logiclab.documentcontrolsystem.mapper.DocumentVersionMapper;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DocumentVersionService {
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogService auditLogService;
    private final DocumentVersionMapper documentVersionMapper;

    @Transactional
    public DocumentVersion createDraftVersion(CreateVersionRequest request, User currentUser){
        checkAuthorOrAdminOrReviewer(currentUser);
        validateRequest(request);

        Document document = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new RuntimeException("Document not found!"));

        DocumentVersion newVersion = new DocumentVersion();

        DocumentVersion lastVersion = documentVersionRepository
                .findTopByDocumentOrderByVersionNumberDesc(document)
                .orElseThrow(() -> new RuntimeException("Document has no versions!"));

        int nextVersionNumber = lastVersion.getVersionNumber()+1;

        newVersion.setDocument(document);
        newVersion.setVersionNumber(nextVersionNumber);
        newVersion.setParentVersion(findActive(document.getVersions()));
        newVersion.setStatus(VersionStatus.DRAFT);
        newVersion.setActive(false);
        newVersion.setCreatedBy(currentUser);
        newVersion.setCreatedAt(LocalDateTime.now());
        newVersion.setContent(request.getContent());
        newVersion.setExtension(request.getExtension());
        newVersion.setChangeSummary(request.getChangeSummary());

        DocumentVersion savedDocumentVersion = documentVersionRepository.save(newVersion);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.DOCUMENT_VERSION,
                newVersion.getId(),
                currentUser.getUsername() + " created document version draft with ID: " + savedDocumentVersion.getId()
        );

        return savedDocumentVersion;
    }

    @Transactional
    public DocumentVersionResponse submitForReview(int versionId, User currentUser) {
        DocumentVersion version = documentVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found!"));

        if (version.getStatus() != VersionStatus.DRAFT) {
            throw new RuntimeException("Only draft versions can be submitted for review!");
        }

        if (!version.getCreatedBy().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to submit this version for review!");
        }

        version.setStatus(VersionStatus.IN_REVIEW);

        auditLogService.log(
                currentUser,
                AuditAction.PUBLISHED_FOR_REVIEW,
                AuditEntityType.DOCUMENT_VERSION,
                version.getId(),
                currentUser.getUsername() + " submitted document version for review with ID: " + version.getId()
        );

        return  documentVersionMapper.toResponse(documentVersionRepository.save(version));
    }

    public List<DocumentVersionResponse> getVersionsByDocumentId(int documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException();
        }

        return  documentVersionMapper.toResponseList(documentVersionRepository.findByDocumentId(documentId));
    }

    public DocumentVersionResponse getActiveVersionByDocumentId(int documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException();
        }

        return  documentVersionMapper.toResponse(documentVersionRepository.findByDocumentIdAndIsActiveTrue(documentId)
                .orElseThrow(NoActiveVersionFoundException::new));
    }

    public DocumentVersionResponse getLatestVersionByDocumentId(int documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new DocumentNotFoundException();
        }

        return  documentVersionMapper.toResponse(documentVersionRepository.findTopByDocumentIdOrderByVersionNumberDesc(documentId)
                .orElseThrow(NoVersionsException::new));
    }

    public DocumentVersionResponse getById(int versionId) {
        return  documentVersionMapper.toResponse(documentVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Document version not found with id: " + versionId)));
    }

    private boolean isAuthor(User user){
        return user.getRole().getName() == RoleName.AUTHOR;
    }

    private boolean isAdmin(User user){
        return user.getRole().getName() ==RoleName.ADMIN;
    }

    private boolean isReviewer(User user){return user.getRole().getName() ==RoleName.REVIEWER;
    }

    private DocumentVersion findActive(List<DocumentVersion> list) {
        return list.stream()
                .filter(DocumentVersion::isActive)
                .findFirst()
                .orElseThrow(NoActiveVersionFoundException::new);
    }
    private void validateRequest(CreateVersionRequest request) {
        if (request.getContent() == null || request.getContent().length == 0) {
            throw new RuntimeException("Content is required!");
        }

        if (request.getExtension() == null || request.getExtension().trim().isEmpty()) {
            throw new RuntimeException("Extension is required!");
        }
        if (!request.getExtension().matches("^[a-zA-Z0-9]+$")) {
            throw new RuntimeException("Invalid file extension!");
        }

        if (request.getChangeSummary() == null || request.getChangeSummary().trim().isEmpty()) {
            throw new RuntimeException("Change summary is required!");
        }
    }

    private void checkAuthorOrAdminOrReviewer(User user){
        if(!(isAdmin(user) || isAuthor(user) || isReviewer(user)))
            throw new NoPermissionException();
    }
}
