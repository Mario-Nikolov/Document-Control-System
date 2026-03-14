package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.AuditLog;
import com.logiclab.documentcontrolsystem.domain.User;

import java.util.List;

public interface AuditLogService {
    AuditLog createLog(User currentUser);
    void rollback();
    List<AuditLog> showAuditLog();
}
