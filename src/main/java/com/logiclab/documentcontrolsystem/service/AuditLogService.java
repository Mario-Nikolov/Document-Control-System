package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.response.AuditLogResponse;
import com.logiclab.documentcontrolsystem.exceptions.NoPermissionException;
import com.logiclab.documentcontrolsystem.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Getter
@Setter
@AllArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(User user, AuditAction action, AuditEntityType entityType, Integer entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
    }

    public List<AuditLogResponse> getAllLogs(User currentUser) {
        checkIsAdmin(currentUser);
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(auditLog -> {
                    AuditLogResponse response = new AuditLogResponse();

                    response.setId(auditLog.getId());
                    response.setUserId(auditLog.getUser().getId());
                    response.setUsername(auditLog.getUser().getUsername());
                    response.setAction(auditLog.getAction());
                    response.setEntityType(auditLog.getEntityType());
                    response.setEntityId(auditLog.getEntityId());
                    response.setDetails(auditLog.getDetails());
                    response.setCreatedAt(auditLog.getCreatedAt());

                    return response;
                })
                .toList();
    }

    private void checkIsAdmin(User user) {
        if (user.getRole().getName() != RoleName.ADMIN) {
            throw new NoPermissionException();
        }
    }
}
