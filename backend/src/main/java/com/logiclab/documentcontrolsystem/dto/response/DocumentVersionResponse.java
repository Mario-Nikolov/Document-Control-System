package com.logiclab.documentcontrolsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DocumentVersionResponse {
    private Integer id;
    private Integer documentId;
    private Integer versionNumber;
    private String title;
    private String description;
    private Integer parentVersionId;
    private String status;
    private boolean active;
    private String createdByUsername;
    private String changeSummary;
    private byte[] content;
    private String text;
    private String extension;
    private LocalDateTime createdAt;
}
