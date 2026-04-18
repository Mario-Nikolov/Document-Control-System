package com.logiclab.documentcontrolsystem.dto.response;

import com.logiclab.documentcontrolsystem.domain.DiffType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DiffLineResponse {
    private DiffType type;
    private String oldLine;
    private String newLine;
}
