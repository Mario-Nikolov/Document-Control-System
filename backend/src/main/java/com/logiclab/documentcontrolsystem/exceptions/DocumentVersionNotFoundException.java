package com.logiclab.documentcontrolsystem.exceptions;

public class DocumentVersionNotFoundException extends RuntimeException {
    public DocumentVersionNotFoundException() {
        super("Document version not found!");
    }
}
