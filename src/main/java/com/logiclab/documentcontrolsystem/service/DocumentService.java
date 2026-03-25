package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Document createDraft(CreateDocumentRequest request, User currentUser) throws IOException {
        if (!(isAuthor(currentUser) || isAdmin(currentUser))) {
            throw new RuntimeException("You don't have permission to perform this action!");
        }

        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new RuntimeException("Document content is required!");
        }

        if (documentRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("File with this title already exists!");
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
        version.setContent(request.getContent().getBytes());
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
    public Document publishDocument(int documentId, User currentUser) {
        Document document = getDocumentById(documentId);

        if (!haveRights(document, currentUser)) {
            throw new RuntimeException("You don't have permission to publish this document!");
        }

        DocumentVersion draftVersion = documentVersionRepository
                .findTopByDocumentOrderByVersionNumberDesc(document)
                .orElseThrow(() -> new RuntimeException("Document has no versions!"));

        if (draftVersion.getStatus() != VersionStatus.DRAFT) {
            throw new RuntimeException("Only draft documents can be published!");
        }

        if (document.getActiveVersion() != null) {
            throw new RuntimeException("Document is already published!");
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

        return document;
    }

    @Transactional
    public void deleteDocument(int documentId, User currentUser){
        Document document = getDocumentById(documentId);

        if (!haveRights(document, currentUser)) {
            throw new RuntimeException("You don't have permission to delete this document!");
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

    public boolean haveRights(Document document,User currentUser){
        return isAuthorOfTheDoc(document,currentUser) || isAdmin(currentUser);
    }

    public boolean isAuthorOfTheDoc(Document document, User user){
        return Objects.equals(document.getCreatedBy().getId(), user.getId());
    }
    public boolean isAuthor(User user){
        return user.getRoles().stream().anyMatch(role -> role.getName()==RoleName.AUTHOR);
    }
    public boolean isAdmin(User user){
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);
    }

    public Document getDocumentById(int documentId){
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("No such document in the database!"));
    }
}
