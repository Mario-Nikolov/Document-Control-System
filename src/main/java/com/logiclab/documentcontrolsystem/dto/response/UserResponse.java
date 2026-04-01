package com.logiclab.documentcontrolsystem.dto.response;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
