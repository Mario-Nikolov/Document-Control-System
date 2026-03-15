package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    List<AuditLog> findByUserId(Integer userId);

    List<AuditLog> findByAction(String action);
}