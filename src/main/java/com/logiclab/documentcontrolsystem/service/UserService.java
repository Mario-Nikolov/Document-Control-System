package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    User createUser(CreateUserRequest request, String authHeader);
    void addRole(int id, String authHeader, RoleName roleName);
    void removeRole(int id, String authHeader, RoleName roleName);
    void deleteUser(int id,String authHeader);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(int id);

}
