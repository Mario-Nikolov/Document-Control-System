package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateDocumentRequest;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;

    public DocumentService(DocumentRepository documentRepository, DocumentVersionRepository documentVersionRepository){
        this.documentRepository = documentRepository;
        this.documentVersionRepository = documentVersionRepository;
    }

    public Document createDocument(CreateDocumentRequest request, User currentUser){
        if(!(isAuthor(currentUser) || isAdmin(currentUser)))
            throw new RuntimeException("You don't have permission to perform this action!");

        List<Document> all = documentRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setCreatedBy(currentUser);
        document.setCreatedAt(now);
        document.setUpdatedAt(now);

        DocumentVersion version = new DocumentVersion();
        version.setDocument(document);
        version.setVersionNumber(1);
        version.setParentVersion(null);
        version.setStatus(VersionStatus.ACTIVE);
        version.setActive(true);
        version.setCreatedBy(currentUser);
        version.setCreatedAt(now);
        version.setContent(request.getContent());
        version.setChangeSummary("Initial version");

        documentRepository.save(document);
        documentVersionRepository.save(version);
        document.setActiveVersion(version);
        documentRepository.save(document);

        return  document;
    }

    public void deleteDocument(int documentId, User currentUser){
        Document document = getDocumentById(documentId);

        if (!haveRights(document, currentUser)) {
            throw new RuntimeException("You don't have permission to delete this document!");
        }

        documentRepository.delete(document);
    }

    public boolean haveRights(Document document,User currentUser){
        return isAuthorOfTheDoc(document,currentUser) || isAdmin(currentUser);
    }

    public boolean isAuthorOfTheDoc(Document document, User user){
        return document.getCreatedBy().getId()==user.getId();
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
