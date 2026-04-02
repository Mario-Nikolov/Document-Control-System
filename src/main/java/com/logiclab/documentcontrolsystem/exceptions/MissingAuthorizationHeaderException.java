package com.logiclab.documentcontrolsystem.exceptions;

public class MissingAuthorizationHeaderException extends RuntimeException {
    public MissingAuthorizationHeaderException() {
        super("Missing authorization header!");
    }
}
