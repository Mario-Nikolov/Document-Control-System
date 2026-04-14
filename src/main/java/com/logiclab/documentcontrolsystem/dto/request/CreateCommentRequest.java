package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;

@Getter
public class CreateCommentRequest {
    private Integer documentVersionId;
    private String body;
}
