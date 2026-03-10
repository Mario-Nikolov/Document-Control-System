package com.logiclab.documentcontrolsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersion {
    private Long id;
    private Document document;
    private DocumentVersion parentVersion;
    private VersionStatus status;
    private LocalDateTime createdAt;
    private User createdBy;
}
