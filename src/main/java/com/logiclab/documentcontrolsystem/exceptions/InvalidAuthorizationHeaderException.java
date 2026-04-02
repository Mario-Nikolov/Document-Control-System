package com.logiclab.documentcontrolsystem.exceptions;

public class InvalidAuthorizationHeaderException extends RuntimeException {
    public InvalidAuthorizationHeaderException() {
        super("Invalid authorization header!");
    }
}
