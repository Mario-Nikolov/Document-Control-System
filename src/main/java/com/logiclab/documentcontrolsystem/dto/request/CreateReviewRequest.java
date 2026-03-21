package com.logiclab.documentcontrolsystem.dto.request;

import com.logiclab.documentcontrolsystem.domain.DocumentVersion;
import com.logiclab.documentcontrolsystem.domain.VersionDecision;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    private DocumentVersion documentVersion;
    private String comment;
    private VersionDecision versionDecision;
}
