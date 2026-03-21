package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.repository.DocumentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentVersionService {
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public DocumentVersion createNewVersion(Document document, User currentUser){
        checkAuthorOrAdmin(currentUser);
        DocumentVersion newVersion = new DocumentVersion();
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
}
