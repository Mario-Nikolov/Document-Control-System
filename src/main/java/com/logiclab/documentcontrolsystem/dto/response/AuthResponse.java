package com.logiclab.documentcontrolsystem.dto.response;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String message;
    private Integer userId;
    private String username;
    private String email;
    private Set<String> roles;
    private String accessToken;
}
