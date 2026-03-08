package com.logiclab.documentcontrolsystem.domain;

public class User {
    private final String username;
    private final String password;
    private final Role role;

    public User(String u, String p,Role r){
        username=u;
        password=p;
        role=r;
    }
}
