package com.logiclab.documentcontrolsystem.exceptions;

public class NoVersionsException extends RuntimeException {
    public NoVersionsException() {
        super("Document has no versions!");
    }
}
