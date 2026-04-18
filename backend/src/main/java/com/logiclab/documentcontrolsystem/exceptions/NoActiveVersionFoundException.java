package com.logiclab.documentcontrolsystem.exceptions;

public class NoActiveVersionFoundException extends RuntimeException {
    public NoActiveVersionFoundException() {
        super("Active version not found!");
    }
}
