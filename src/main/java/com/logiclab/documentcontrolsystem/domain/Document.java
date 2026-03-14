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
public class Document {
    private int id;
    private String title;
    private String description;
    private String filePath;
    private User createdBy;
    private int activeVersionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
