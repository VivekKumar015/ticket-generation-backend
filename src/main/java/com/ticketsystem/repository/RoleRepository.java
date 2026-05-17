package com.ticketsystem.repository;

import com.ticketsystem.entity.Role;
import com.ticketsystem.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
    Boolean existsByName(RoleName name);
}