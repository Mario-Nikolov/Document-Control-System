package com.logiclab.documentcontrolsystem.exceptions;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException() {
        super("Document not found! ");
    }
}
