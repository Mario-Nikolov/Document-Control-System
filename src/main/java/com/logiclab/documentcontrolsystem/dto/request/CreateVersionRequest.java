package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;

@Getter
public class CreateVersionRequest {
    private int documentId;
    private byte[] content;
    private String extension;
    private String changeSummary;
}
