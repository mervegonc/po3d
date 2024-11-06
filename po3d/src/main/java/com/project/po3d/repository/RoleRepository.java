package com.project.po3d.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.po3d.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
