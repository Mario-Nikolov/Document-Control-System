package com.logiclab.documentcontrolsystem.exceptions;

public class ExistByEmailException extends RuntimeException {
    public ExistByEmailException() {
        super("An account with this email already exists!");
    }
}
