package com.logiclab.documentcontrolsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {
    private String title;
    private String description;
    private MultipartFile content;
}
