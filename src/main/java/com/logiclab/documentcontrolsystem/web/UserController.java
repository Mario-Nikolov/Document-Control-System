package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.AddRoleRequest;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;
import com.logiclab.documentcontrolsystem.mapper.UserMapper;
import com.logiclab.documentcontrolsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request,
            @RequestHeader("Authorization")String authHeader
    ) {
        User createdUser = userService.createUser(request, authHeader);
        UserResponse response = userMapper.toResponse(createdUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable int id,
            @RequestHeader("Authorization")String authHeader
    ) {
        userService.deleteUser(id,authHeader );
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @PostMapping("/addrole")
    public ResponseEntity<MessageResponse> addRole(
            @RequestBody AddRoleRequest request,
            @RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(userService.addRole(request,authHeader));
    }


}