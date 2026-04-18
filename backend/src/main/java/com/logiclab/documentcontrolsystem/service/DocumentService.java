package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.dto.response.DocumentResponse;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.exceptions.*;
import com.logiclab.documentcontrolsystem.mapper.DocumentMapper;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final AuditLogService auditLogService;
    private final DocumentMapper documentMapper;

    @Transactional
    public Document createDocument(CreateDocumentRequest request, User currentUser) {
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
        version.setStatus(VersionStatus.ACTIVE);
        version.setActive(true);
        version.setCreatedBy(currentUser);
        version.setCreatedAt(LocalDateTime.now());
        version.setContent(request.getContent());
        version.setChangeSummary("Initial document");

        documentVersionRepository.save(version);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.DOCUMENT,
                document.getId(),
                currentUser.getUsername() + " created document  with ID: " + document.getId()
        );

        return document;
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
    public Document getDocumentById(Integer documentId){
        return documentRepository.findById(documentId)
                .orElseThrow(DocumentNotFoundException::new);
    }

    public Document getByDocumentTitle(String title){
        return documentRepository.findByTitle(title).
                orElseThrow(DocumentNotFoundException::new);
    }

    public List<Document> getByCreatedById(Integer id){
        List<Document> docs = documentRepository.findByCreatedById(id);

        if(docs.isEmpty()){
            throw new DocumentNotFoundException();
        }

        return docs;
    }

    @Transactional
    public List<DocumentResponse> getAll(){
        List<Document> all = documentRepository.findAll();
        return documentMapper.toResponseList(all);
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
