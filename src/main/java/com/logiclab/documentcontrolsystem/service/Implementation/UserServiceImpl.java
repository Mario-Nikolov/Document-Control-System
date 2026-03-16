package com.logiclab.documentcontrolsystem.service.Implementation;

import com.logiclab.documentcontrolsystem.domain.Role;
import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.repository.RoleRepository;
import com.logiclab.documentcontrolsystem.repository.UserRepository;
import com.logiclab.documentcontrolsystem.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository,RoleRepository roleRepository){
        this.userRepository=userRepository;
        this.roleRepository=roleRepository;
    }

    @Override
    public User createUser(User newUser, User currentUser){
        checkForAdmin(currentUser);

        checkIfExistsByEmail(newUser.getEmail());

        checkIfExistsByUsername(newUser.getUsername());

        return userRepository.save(newUser);
    }

    @Override
    public void deleteUser(int id, User currentUser){
        checkForAdmin(currentUser);

        if(!userRepository.existsById(id))
            throw new RuntimeException("Wrong user id or no such user in the database!");        //Трябва да се направи ексепшън

        userRepository.deleteById(id);
    }

    @Override
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
    }

    @Override
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
}
