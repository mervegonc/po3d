package com.project.po3d.repository;

import com.project.po3d.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
}
