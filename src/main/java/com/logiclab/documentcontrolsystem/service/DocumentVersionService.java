package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateVersionRequest;
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

    @Transactional
    public DocumentVersion createNewVersion(CreateVersionRequest request, User currentUser){
        checkAuthorOrAdmin(currentUser);
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
        newVersion.setStatus(VersionStatus.IN_REVIEW);
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
                currentUser + " created document version with ID: " + newVersion.getId()
        );

        return savedDocumentVersion;
    }
    private boolean isAuthor(User user){
        return user.getRoles().stream().anyMatch(role -> role.getName()== RoleName.AUTHOR);
    }

    private boolean isAdmin(User user){
        return user.getRoles().stream().anyMatch(role -> role.getName()==RoleName.ADMIN);
    }

    private void checkAuthorOrAdmin(User user){
        if(!(isAdmin(user) || isAuthor(user)))
            throw new RuntimeException("You don't have permission to perform this action!");
    }

    public DocumentVersion getById(int versionId) {
        return documentVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Document version not found with id: " + versionId));
    }

    private DocumentVersion findActive(List<DocumentVersion> list) {
        return list.stream()
                .filter(DocumentVersion::isActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active version found!"));
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
}
