package com.logiclab.documentcontrolsystem.dto.request;

import com.logiclab.documentcontrolsystem.domain.ReviewDecision;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    private int documentVersionId;
    private String comment;
    private ReviewDecision reviewDecision;
}
