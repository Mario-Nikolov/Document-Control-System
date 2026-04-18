package com.logiclab.documentcontrolsystem.dto.response;

import com.logiclab.documentcontrolsystem.domain.AuditAction;
import com.logiclab.documentcontrolsystem.domain.AuditEntityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private Integer id;
    private Integer userId;
    private String username;
    private AuditAction action;
    private AuditEntityType entityType;
    private Integer entityId;
    private String details;
    private LocalDateTime createdAt;
}