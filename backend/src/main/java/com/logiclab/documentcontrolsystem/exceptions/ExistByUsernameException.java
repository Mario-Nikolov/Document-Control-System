package com.logiclab.documentcontrolsystem.exceptions;

public class ExistByUsernameException extends RuntimeException {
    public ExistByUsernameException() {
        super("An user with this username already exists!");
    }
}
