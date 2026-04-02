package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.ChangeRoleRequest;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
import com.logiclab.documentcontrolsystem.dto.response.MessageResponse;
import com.logiclab.documentcontrolsystem.dto.response.UserResponse;
import com.logiclab.documentcontrolsystem.exceptions.*;
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
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email +  " not found!"));

        checkForAdmin(currentUser);

        checkIfExistsByEmail(request.getEmail());
        checkIfExistsByUsername(request.getUsername());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new RoleNotFoundException("Role: " + request.getRoleName() + " not found: " ));

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
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email +  " not found!"));

        checkForAdmin(currentUser);

        if(!userRepository.existsById(id))
            throw new UserNotFoundException("User with id: " + id +  " not found!");

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
                .orElseThrow(() -> new UserNotFoundException("User with id: " + id +  " not found!"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public MessageResponse changeRole(ChangeRoleRequest request, String authHeader){
        String token = extractToken(authHeader);

        String email = jwtService.extractEmail(token);

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email +  " not found!"));

        checkForAdmin(currentUser);

        if (request.getId() == null) {
            throw new InvalidUserDataException("User id is required!");
        }

        if (request.getRoleName() == null) {
            throw new InvalidUserDataException("Role id is required!");
        }

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("User with id: " + request.getId() +  " not found!"));

        Role newRole = roleRepository.findByName(request.getRoleName())
                .orElseThrow(()-> new RoleNotFoundException("Role not found!"));

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
            throw new NoPermissionException();
    }

    private void checkIfExistsByEmail(String email){
        if(userRepository.existsByEmail(email))
            throw new ExistByEmailException();
    }

    private void checkIfExistsByUsername(String username){
        if(userRepository.existsByUsername(username))
            throw new ExistByUsernameException();
    }


    private String extractToken(String authHeader) {
        if (authHeader == null )
            throw new MissingAuthorizationHeaderException();

        if(!authHeader.startsWith("Bearer "))
            throw new InvalidAuthorizationHeaderException();

        return authHeader.substring(7);
    }
}
