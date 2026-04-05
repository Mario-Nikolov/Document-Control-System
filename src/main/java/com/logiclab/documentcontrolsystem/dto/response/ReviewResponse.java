package com.logiclab.documentcontrolsystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Integer id;
    private Integer documentVersionId;
    private String reviewerUsername;
    private String decision;
    private String comment;
    private LocalDateTime reviewedAt;
}