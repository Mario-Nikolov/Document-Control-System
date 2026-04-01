package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.ChangeRoleRequest;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    User createUser(CreateUserRequest request, String authHeader);
    void deleteUser(int id,String authHeader);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(int id);
    MessageResponse changeRole(ChangeRoleRequest request, String authHeader);

}
