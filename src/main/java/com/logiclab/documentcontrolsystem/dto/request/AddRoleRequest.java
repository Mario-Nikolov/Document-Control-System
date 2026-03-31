package com.logiclab.documentcontrolsystem.dto.request;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import lombok.Getter;

@Getter
public class AddRoleRequest {
    private Integer id;
    private RoleName roleName;
}
