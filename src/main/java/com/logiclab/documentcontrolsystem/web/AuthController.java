package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.dto.request.LoginRequest;
import com.logiclab.documentcontrolsystem.dto.response.AuthResponse;
import com.logiclab.documentcontrolsystem.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}