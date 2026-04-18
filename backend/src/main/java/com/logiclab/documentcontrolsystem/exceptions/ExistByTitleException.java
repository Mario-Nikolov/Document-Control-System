package com.logiclab.documentcontrolsystem.exceptions;

public class ExistByTitleException extends RuntimeException {
    public ExistByTitleException() {
        super("File with this title already exists! ");
    }
}
