package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateVersionRequest {
    private Integer documentId;
    private byte[] content;
    private String extension;
    private String changeSummary;
}
