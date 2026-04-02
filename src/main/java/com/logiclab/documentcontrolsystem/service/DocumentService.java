package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.exceptions.*;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final AuditLogService auditLogService;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Transactional
    public Document createDraft(CreateDocumentRequest request, User currentUser) {
        if (!(isAuthor(currentUser) || isAdmin(currentUser) || isReviewer(currentUser))) {
            throw new NoPermissionException();
        }

        if (request.getContent() == null) {
            throw new InvalidDocumentDataException("Content is required!");
        }

        if (documentRepository.existsByTitle(request.getTitle())) {
            throw new ExistByTitleException();
        }

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setCreatedBy(currentUser);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        documentRepository.save(document);

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber(1);
        version.setParentVersion(null);
        version.setStatus(VersionStatus.DRAFT);
        version.setActive(false);
        version.setCreatedBy(currentUser);
        version.setCreatedAt(LocalDateTime.now());
        version.setContent(request.getContent());
        version.setChangeSummary("Initial draft");

        documentVersionRepository.save(version);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.DOCUMENT,
                document.getId(),
                currentUser.getUsername() + " created document draft with ID: " + document.getId()
        );

        return document;
    }

    @Transactional
    public MessageResponse publishDocument(int documentId, User currentUser) {
        Document document = getDocumentById(documentId);

        if (haveRights(document, currentUser)) {
            throw new NoPermissionException();
        }

        DocumentVersion draftVersion = documentVersionRepository
                .findTopByDocumentOrderByVersionNumberDesc(document)
                .orElseThrow(NoVersionsException::new);

        if (draftVersion.getStatus() != VersionStatus.DRAFT) {
            throw new DocumentNotDraftException();
        }

        if (document.getActiveVersion() != null) {
            throw new DocumentAlreadyPublishedException();
        }

        draftVersion.setStatus(VersionStatus.ACTIVE);
        draftVersion.setActive(true);

        document.setActiveVersion(draftVersion);
        document.setUpdatedAt(LocalDateTime.now());

        documentVersionRepository.save(draftVersion);
        documentRepository.save(document);

        auditLogService.log(
                currentUser,
                AuditAction.PUBLISHED,
                AuditEntityType.DOCUMENT,
                document.getId(),
                currentUser.getUsername() + " published document with ID: " + document.getId()
        );

        return new MessageResponse("Successfully published " + document.getTitle());
    }

    @Transactional
    public void deleteDocument(int documentId, User currentUser){
        Document document = getDocumentById(documentId);

        if (haveRights(document, currentUser)) {
            throw new NoPermissionException();
        }

        documentRepository.delete(document);

        auditLogService.log(
                currentUser,
                AuditAction.DELETE,
                AuditEntityType.DOCUMENT,
                documentId,
                currentUser.getUsername() + " deleted document with ID: " + documentId
        );
    }
    public Document getDocumentById(int documentId){
        return documentRepository.findById(documentId)
                .orElseThrow(DocumentNotFoundException::new);
    }

    private boolean haveRights(Document document,User currentUser){
        return !isAuthorOfTheDoc(document, currentUser) && !isAdmin(currentUser);
    }

    private boolean isAuthorOfTheDoc(Document document, User user){
        return Objects.equals(document.getCreatedBy().getId(), user.getId());
    }
    private boolean isAuthor(User user){
        return user.getRole().getName()==RoleName.AUTHOR;
    }
    private boolean isAdmin(User user){
        return user.getRole().getName()== RoleName.ADMIN;
    }

    private boolean isReviewer(User user){
        return user.getRole().getName()== RoleName.REVIEWER;
    }
}
