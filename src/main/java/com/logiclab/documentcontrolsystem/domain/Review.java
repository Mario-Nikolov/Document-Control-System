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
    private Long id;
    private DocumentVersion documentVersion;
    private User reviewer;
    private String comment;
    private VersionStatus decision;     //Трябва да се направи промяна по логиката
    private LocalDateTime createdAt;
}