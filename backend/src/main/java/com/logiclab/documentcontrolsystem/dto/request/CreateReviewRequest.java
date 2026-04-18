package com.logiclab.documentcontrolsystem.dto.request;

import com.logiclab.documentcontrolsystem.domain.ReviewDecision;
import lombok.Getter;

@Getter

public class CreateReviewRequest {
    private Integer documentVersionId;
    private String comment;
    private ReviewDecision reviewDecision;
}
