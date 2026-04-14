package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;

@Getter
public class EditCommentRequest {
    private Integer commentId;
    private String body;
}
