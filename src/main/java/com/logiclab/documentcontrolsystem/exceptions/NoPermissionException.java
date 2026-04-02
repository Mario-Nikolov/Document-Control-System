package com.logiclab.documentcontrolsystem.exceptions;

public class NoPermissionException extends RuntimeException {
    public NoPermissionException() {
        super("You don't have permission to perform this action!");
    }
}
