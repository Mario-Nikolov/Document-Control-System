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
    private Long id;
    private String title;
    private String description;
    private String file;
    private LocalDateTime createdAt;
    private User createdBy;
}
