package com.logiclab.documentcontrolsystem.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CreateDocumentRequest {
    private String title;
    private String description;
    private byte[] content;
    private String extension;
}
