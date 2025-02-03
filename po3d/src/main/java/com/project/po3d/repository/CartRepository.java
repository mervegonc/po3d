package com.project.po3d.repository;

import com.project.po3d.entity.Cart;
import com.project.po3d.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    
    Optional<Cart> findByUser(User user); // `userId` yerine `User user` kullandık.
}
