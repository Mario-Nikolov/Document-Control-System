package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.response.AuditLogResponse;
import com.logiclab.documentcontrolsystem.service.AuditLogService;
import com.logiclab.documentcontrolsystem.service.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@AllArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final JWTService jwtService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs(
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = jwtService.extractUser(authHeader);
        return ResponseEntity.ok(auditLogService.getAllLogs(currentUser));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByUserId(
            @PathVariable Integer userId,
            @RequestHeader("Authorization") String authHeader){

        User currentUser = jwtService.extractUser(authHeader);
        return ResponseEntity.ok(auditLogService.getAllLogsByUserId(userId,currentUser));
        }
    }