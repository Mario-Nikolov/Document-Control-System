package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.User;

public interface UserService {
    User createUser(User currentUser);
    User updateUser(int id,User UpdatedUser, User currentUser);
    void changeRole(int id,User currentUser);
    void deleteUser(int id,User currentUser);

}
