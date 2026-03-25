package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
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
@RequestMapping
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal User currentUser
    ){
        User createdUser = userService.createUser(request,currentUser);
        UserResponse response = userMapper.toResponse(createdUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResponse> response = userMapper.toResponseList(users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable int id,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<MessageResponse> addRole(
            @PathVariable int id,
            @RequestParam RoleName roleName,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.addRole(id, currentUser, roleName);
        return ResponseEntity.ok(new MessageResponse("Role added successfully"));
    }

    @DeleteMapping("/{id}/roles")
    public ResponseEntity<MessageResponse> removeRole(
            @PathVariable int id,
            @RequestParam RoleName roleName,
            @AuthenticationPrincipal User currentUser
    ) {
        userService.removeRole(id, currentUser, roleName);
        return ResponseEntity.ok(new MessageResponse("Role removed successfully"));
    }
}