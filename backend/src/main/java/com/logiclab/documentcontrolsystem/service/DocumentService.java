package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.dto.response.DocumentResponse;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.exceptions.*;
import com.logiclab.documentcontrolsystem.mapper.DocumentMapper;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import com.logiclab.documentcontrolsystem.repository.ReviewRepository;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    @PersistenceContext
    private EntityManager entityManager;

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

        LocalDateTime now = LocalDateTime.now();

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setCreatedBy(currentUser);
        document.setCreatedAt(now);
        document.setUpdatedAt(now);

        Document savedDocument = documentRepository.save(document);

        DocumentVersion version = new DocumentVersion();
        version.setDocument(savedDocument);
        version.setVersionNumber(1);
        version.setParentVersion(null);
        version.setStatus(VersionStatus.ACTIVE);
        version.setActive(true);
        version.setCreatedBy(currentUser);
        version.setCreatedAt(now);
        version.setContent(request.getContent());
        version.setExtension(request.getExtension());
        version.setChangeSummary("Initial document");

        DocumentVersion savedVersion = documentVersionRepository.save(version);

        savedDocument.setActiveVersion(savedVersion);
        savedDocument.setUpdatedAt(now);

        return documentRepository.save(savedDocument);
    }

    @Transactional
    public void deleteDocument(int documentId, User currentUser){
        Document document = getDocumentById(documentId);

        if (haveRights(document, currentUser)) {
            throw new NoPermissionException();
        }

//        reviewRepository.deleteByVersionId(document.);
//        documentVersionRepository.deleteById(documentId);
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
