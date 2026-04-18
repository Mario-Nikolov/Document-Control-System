package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.response.AuditLogResponse;
import com.logiclab.documentcontrolsystem.exceptions.AuditLogsNotFoundException;
import com.logiclab.documentcontrolsystem.exceptions.NoPermissionException;
import com.logiclab.documentcontrolsystem.repository.AuditLogRepository;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
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
    private final UserRepository userRepository;

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
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUser() != null ? log.getUser().getId() : null,
                        log.getUser() != null ? log.getUser().getUsername() : "Deleted user",
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getCreatedAt()
                ))
                .toList();
    }

    public List<AuditLogResponse>getAllLogsByUserId(Integer userId, User currentUser){
        checkIsAdmin(currentUser);

        if (!userRepository.existsById(userId)) {
            return List.of();
        }

        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getUser() != null ? log.getUser().getId() : null,
                        log.getUser() != null ? log.getUser().getUsername() : "Deleted user",
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getDetails(),
                        log.getCreatedAt()
                ))
                .toList();

    }

    private void checkIsAdmin(User user) {
        if (user.getRole().getName() != RoleName.ADMIN) {
            throw new NoPermissionException();
        }
    }
}
