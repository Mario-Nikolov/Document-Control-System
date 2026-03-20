package com.logiclab.documentcontrolsystem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {
    private String title;
    private String description;
    private String content;
}
