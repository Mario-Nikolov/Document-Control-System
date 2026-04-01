package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.ChangeRoleRequest;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;
import com.logiclab.documentcontrolsystem.mapper.UserMapper;
import com.logiclab.documentcontrolsystem.repository.RoleRepository;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final JWTService jwtService;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public User createUser(CreateUserRequest request, String authHeader){
        String token = extractToken(authHeader);

        String email = jwtService.extractEmail(token);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        checkForAdmin(currentUser);

        checkIfExistsByEmail(request.getEmail());
        checkIfExistsByUsername(request.getUsername());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRoleName()));

        newUser.setRole(role);
        newUser.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(newUser);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.USER,
                savedUser.getId(),
                currentUser.getUsername() + " created user with username: " + savedUser.getUsername()
        );

        return savedUser;
    }

    @Override
    @Transactional
    public void deleteUser(int id, String authHeader){
        String token = extractToken(authHeader);

        String email = jwtService.extractEmail(token);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        checkForAdmin(currentUser);

        if(!userRepository.existsById(id))
            throw new RuntimeException("Wrong user id or no such user in the database!");        //Трябва да се направи ексепшън

        userRepository.deleteById(id);

        auditLogService.log(
                currentUser,
                AuditAction.DELETE,
                AuditEntityType.USER,
                id,
                currentUser.getUsername() + " deleted user with id: " + id
        );
    }

    @Override
    @Transactional
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseList(users);
    }

    @Override
    @Transactional
    public UserResponse getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public MessageResponse changeRole(ChangeRoleRequest request, String authHeader){
        String token = extractToken(authHeader);

        String email = jwtService.extractEmail(token);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        checkForAdmin(currentUser);

        if (request.getId() == null) {
            throw new RuntimeException("User id is required!");
        }

        if (request.getRoleName() == null) {
            throw new RuntimeException("Role id is required!");
        }

        if(!userRepository.existsById(request.getId()))
            throw new RuntimeException("Wrong user id or no such user in the database!");

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Wrong user id or no such user in the database!"));      //Трябва да се направи ексепшън

        Role newRole = roleRepository.findByName(request.getRoleName())
                .orElseThrow(()-> new RuntimeException("Role not found!"));

        user.setRole(newRole);

        userRepository.save(user);

        auditLogService.log(
                currentUser,
                AuditAction.EDIT,
                AuditEntityType.USER,
                user.getId(),
                currentUser.getUsername() + " changed role of " + user.getUsername() + " to " + newRole.getName()
        );

        return new MessageResponse("Successfully changed role of " + user.getUsername());

    }

    private void checkForAdmin(User user){
        boolean isAdmin = user.getRole().getName()==RoleName.ADMIN;

        if(!isAdmin)
            throw new RuntimeException("You don't have permission to perform this action!");
    }

    private void checkIfExistsByEmail(String email){
        if(userRepository.existsByEmail(email))
            throw new RuntimeException("An account with this email already exists!");       //Трябва да се направи ексепшън
    }

    private void checkIfExistsByUsername(String username){
        if(userRepository.existsByUsername(username))
            throw new RuntimeException("This username is already taken!");       //Трябва да се направи ексепшън
    }


    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header!");
        }
        return authHeader.substring(7);
    }
}
