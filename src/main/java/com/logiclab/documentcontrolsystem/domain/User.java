package com.logiclab.documentcontrolsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private  int id;
    private String username;
    private String email;
    private String passwordHash;
    private Set<Role> roles;
    private LocalDateTime createdAt;
}
