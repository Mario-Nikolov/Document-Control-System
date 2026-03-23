package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;

@Getter
public class EditCommentRequest {
    private int commentId;
    private String body;
}
