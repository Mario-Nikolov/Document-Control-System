package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.domain.User;

public interface DocumentVersionService {
    DocumentVersion createVersion(User currentUser);
    DocumentVersion editVersion(int documentId,DocumentVersion editedDocumentVersion,User currentUser);
    void deleteVersion(int documentId,User currentUser);
}
