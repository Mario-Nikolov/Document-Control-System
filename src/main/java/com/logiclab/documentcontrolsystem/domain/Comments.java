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
public class Comments {
    private int id;
    private DocumentVersion documentVersion;
    private User commentedBy;
    private String body;
    private LocalDateTime commentedAt;
}
