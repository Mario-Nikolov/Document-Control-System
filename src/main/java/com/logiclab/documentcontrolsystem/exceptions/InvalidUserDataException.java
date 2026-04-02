package com.logiclab.documentcontrolsystem.exceptions;

public class InvalidUserDataException extends RuntimeException {

    public InvalidUserDataException(String message) {
        super(message);
    }
}