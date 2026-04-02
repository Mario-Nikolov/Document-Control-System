package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CreateDocumentRequest {
    private String title;
    private String description;
    private byte[] content;
    private String extension;
}
