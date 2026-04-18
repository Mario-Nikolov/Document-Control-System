package com.logiclab.documentcontrolsystem.exceptions;

public class CommentBodyEmptyException extends RuntimeException {
    public CommentBodyEmptyException() {
        super("Comment body cannot be empty!");
    }
}
