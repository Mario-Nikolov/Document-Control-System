package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;

@Getter
public class CreateCommentRequest {
    private int documentVersionId;
    private String body;
}
