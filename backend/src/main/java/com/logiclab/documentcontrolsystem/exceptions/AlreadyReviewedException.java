package com.logiclab.documentcontrolsystem.exceptions;

public class AlreadyReviewedException extends RuntimeException {
    public AlreadyReviewedException() {
        super("This version has already been reviewed!");
    }
}
