package com.project.po3d.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.po3d.entity.UserRole;
import java.util.List;
import java.util.UUID;
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(UUID userId);
}
