package com.logiclab.documentcontrolsystem.exceptions;

public class AuditLogsNotFoundException extends RuntimeException {
    public AuditLogsNotFoundException(Integer userId) {
        super("No audit logs found for user with ID " + userId);
    }
}
