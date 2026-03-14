package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.Document;
import com.logiclab.documentcontrolsystem.domain.User;

import java.util.List;

public interface DocumentService {
    Document crate(Document document, User currentUser);
    Document getById(int documentId);
    Document getByTittle(String documentTittle);
    List<Document> getAll();
    void changeActiveVersion(Document document);
    Document update(int documentId,User currentUser);
    void delete(int id,User currentUser);

}
