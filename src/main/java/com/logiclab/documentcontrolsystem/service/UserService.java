package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.Role;
import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;

public interface UserService {
    User createUser(CreateUserRequest request, User currentUser);
    void addRole(int id, User currentUser, RoleName roleName);
    void removeRole(int id, User currentUser, RoleName roleName);
    void deleteUser(int id,User currentUser);

}
