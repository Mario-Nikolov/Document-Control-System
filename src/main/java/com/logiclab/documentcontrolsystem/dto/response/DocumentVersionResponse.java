package com.logiclab.documentcontrolsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

public class DocumentVersionResponse {
    private Integer id;
    private Integer documentId;
    private Integer versionNumber;
    private Integer parentVersionId;
    private String status;
    private boolean active;
    private String createdByUsername;
    private String changeSummary;
    private String extension;
    private LocalDateTime createdAt;
}
