package com.logiclab.documentcontrolsystem.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VersionDiffResponse {
    private Integer oldVersionId;
    private Integer newVersionId;
    private Integer oldVersionNumber;
    private Integer newVersionNumber;
    private Integer documentId;
    private String documentTitle;
    private List<DiffLineResponse> lines;
}