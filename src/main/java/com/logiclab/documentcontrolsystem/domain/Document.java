package com.logiclab.documentcontrolsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private int id;                        //При създаване на документ неговата първа версия е активна
    private String title;                   //Съдържанието на документа е в неговата активна версия
    private String description;
    private User createdBy;
    private int activeVersionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
