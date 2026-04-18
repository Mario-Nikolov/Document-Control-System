package com.logiclab.documentcontrolsystem.dto.request;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import lombok.Getter;

@Getter
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private RoleName roleName;
}
