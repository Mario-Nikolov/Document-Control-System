package com.logiclab.documentcontrolsystem.exceptions;

public class DocumentAlreadyPublishedException extends RuntimeException {
    public DocumentAlreadyPublishedException() {
        super("The document is already published!");
    }
}
