package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.LoginRequest;
import com.logiclab.documentcontrolsystem.dto.response.AuthResponse;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
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

    public AuthResponse login(LoginRequest request){
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required!");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required!");
        }
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!matches) {
            throw new RuntimeException("Invalid username/email or password!");
        }

        return new AuthResponse(
                "Login successful!",
                user.getId(),
                user.getUsername()
        );

    }
}
