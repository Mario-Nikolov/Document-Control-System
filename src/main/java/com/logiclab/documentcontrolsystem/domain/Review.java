package com.logiclab.documentcontrolsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private int id;
    private DocumentVersion documentVersion;
    private User reviewer;
    private VersionDecision decision;
    private String comment;
    private LocalDateTime reviewedAt;
}