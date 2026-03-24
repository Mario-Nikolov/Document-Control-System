package com.logiclab.documentcontrolsystem.dto.response;

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

public class CommentResponse {
    private Integer id;
    private Integer documentVersionId;
    private String body;
    private String commentedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
