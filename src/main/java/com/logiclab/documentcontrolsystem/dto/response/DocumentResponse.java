package com.logiclab.documentcontrolsystem.dto.response;

import com.logiclab.documentcontrolsystem.domain.VersionStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DocumentResponse {
    private Integer id;
    private String title;
    private String description;
    private String createdByUsername;
    private Integer activeVersionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
