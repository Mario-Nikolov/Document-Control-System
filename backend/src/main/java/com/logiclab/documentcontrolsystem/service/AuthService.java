package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.AuditAction;
import com.logiclab.documentcontrolsystem.domain.AuditEntityType;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.LoginRequest;
import com.logiclab.documentcontrolsystem.dto.response.AuthResponse;
import com.logiclab.documentcontrolsystem.exceptions.BadRequestException;
import com.logiclab.documentcontrolsystem.exceptions.NotFoundException;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Setter
@Getter
@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final JWTService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request){
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required!");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required!");
        }
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password!"));

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!matches) {
            throw new BadRequestException("Invalid username/email or password!");
        }

        String token = jwtService.generateToken(user);
        auditLogService.log(user, AuditAction.LOGIN, AuditEntityType.USER,user.getId(),user.getUsername() + " logged in successfully");

        return new AuthResponse(
                "Login successful!",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName(),
                token
        );

    }

    public User extractUserFromHeader(String authHeader){
        String token = jwtService.extractToken(authHeader);

        String email = jwtService.extractEmail(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with email: " + email +  " not found!"));
    }
}
