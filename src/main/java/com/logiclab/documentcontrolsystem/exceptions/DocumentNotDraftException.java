package com.logiclab.documentcontrolsystem.exceptions;

public class DocumentNotDraftException extends RuntimeException {
    public DocumentNotDraftException( ) {
        super("Only draft documents can be published!");
    }
}
