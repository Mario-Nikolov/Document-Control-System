package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.ChangeRoleRequest;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;
import com.logiclab.documentcontrolsystem.mapper.UserMapper;
import com.logiclab.documentcontrolsystem.service.JWTService;
import com.logiclab.documentcontrolsystem.service.UserService;
import com.logiclab.documentcontrolsystem.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request,
            @RequestHeader("Authorization")String authHeader
    ) {
        User currentUser = jwtService.extractUser(authHeader);

        User createdUser = userService.createUser(request, currentUser);
        UserResponse response = userMapper.toResponse(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
        User currentUser = jwtService.extractUser(authHeader);
        userService.deleteUser(id,currentUser);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @PostMapping("/change-role")
    public ResponseEntity<MessageResponse> changeRole(
            @RequestBody ChangeRoleRequest request,
            @RequestHeader("Authorization") String authHeader
            ){
        User currentUser = jwtService.extractUser(authHeader);

        return ResponseEntity.ok(userService.changeRole(request, currentUser));
    }
}