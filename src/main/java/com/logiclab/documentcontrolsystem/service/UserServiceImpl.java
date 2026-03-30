package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateUserRequest;
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


    @Override
    @Transactional
    public User createUser(CreateUserRequest request, User currentUser){
        checkForAdmin(currentUser);

        checkIfExistsByEmail(request.getEmail());
        checkIfExistsByUsername(request.getUsername());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(newUser);

        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.USER,
                savedUser.getId(),
                currentUser+ " created user with username: " + savedUser.getUsername()
        );

        return savedUser;
    }

    @Override
    @Transactional
    public void deleteUser(int id, User currentUser){
        checkForAdmin(currentUser);

        if(!userRepository.existsById(id))
            throw new RuntimeException("Wrong user id or no such user in the database!");        //Трябва да се направи ексепшън

        userRepository.deleteById(id);

        auditLogService.log(
                currentUser,
                AuditAction.DELETE,
                AuditEntityType.USER,
                id,
                currentUser + " deleted user with id: " + id
        );
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id: "+id+" not found!"));
    }

    @Override
    @Transactional
    public void addRole(int id,User currentUser,RoleName roleName){
        checkForAdmin(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wrong user id or no such user in the database!"));      //Трябва да се направи ексепшън

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(()-> new RuntimeException("Role not found!"));

        boolean alreadyHasRole = user.getRoles()
                .stream().anyMatch(r ->r.getName()==roleName);
        if(alreadyHasRole)
            throw new RuntimeException("User already has this role!");

        user.getRoles().add(role);

        userRepository.save(user);

        auditLogService.log(
                currentUser,
                AuditAction.ADD_ROLE,
                AuditEntityType.USER,
                user.getId(),
                 currentUser + " added role " + roleName + " to user with ID: " + user.getId()
        );
    }

    @Override
    @Transactional
    public void removeRole(int id,User currentUser,RoleName roleName){
        checkForAdmin(currentUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wrong user id or no such user in the database!"));      //Трябва да се направи ексепшън

        if(user.getRoles().size() == 1 && user.getRoles().stream().anyMatch(r -> r.getName() == roleName))
            throw new RuntimeException("User must have at least one role!");

        boolean removed = user.getRoles().removeIf(r -> r.getName() == roleName);

        if(!removed)
            throw new RuntimeException("User does not have this role");

        userRepository.save(user);

        auditLogService.log(
                currentUser,
                AuditAction.REMOVE_ROLE,
                AuditEntityType.USER,
                user.getId(),
                currentUser + " removed role " + roleName + " from user with ID: " + user.getId()
        );
    }

    private void checkForAdmin(User user){
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);

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

    @Transactional
    public User createUserAsAdminTest(CreateUserRequest request, int adminId) {
        User currentUser = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found!"));

        checkForAdmin(currentUser);

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());

        return userRepository.save(newUser);
    }
}
