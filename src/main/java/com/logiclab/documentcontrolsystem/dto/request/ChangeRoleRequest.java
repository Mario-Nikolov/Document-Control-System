package com.logiclab.documentcontrolsystem.dto.request;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import lombok.Getter;

@Getter
public class ChangeRoleRequest {
    private Integer id;
    private RoleName roleName;
}
