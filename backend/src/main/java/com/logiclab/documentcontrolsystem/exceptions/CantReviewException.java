package com.logiclab.documentcontrolsystem.exceptions;

public class CantReviewException extends RuntimeException {
    public CantReviewException() {
        super("Can't review this version!");
    }
}
