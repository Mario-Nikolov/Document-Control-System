package com.logiclab.documentcontrolsystem.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found!");
    }
}
