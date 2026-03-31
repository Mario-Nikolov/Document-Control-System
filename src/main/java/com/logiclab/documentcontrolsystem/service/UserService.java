package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.AddRoleRequest;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    User createUser(CreateUserRequest request, String authHeader);
    MessageResponse addRole(AddRoleRequest request, String authHeader);
    void removeRole(int id, String authHeader, RoleName roleName);
    void deleteUser(int id,String authHeader);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(int id);

}
