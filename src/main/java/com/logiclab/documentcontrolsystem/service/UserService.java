package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.Role;
import com.logiclab.documentcontrolsystem.domain.RoleName;
import com.logiclab.documentcontrolsystem.domain.User;

public interface UserService {
    User createUser(User newUser,User currentUser);
    void addRole(int id, User currentUser, RoleName roleName);
    void removeRole(int id, User currentUser, RoleName roleName);
    void deleteUser(int id,User currentUser);

}
