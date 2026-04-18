package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.Role;
import com.logiclab.documentcontrolsystem.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleName name);
}