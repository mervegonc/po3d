package com.project.po3d.business.abstracts;

import com.project.po3d.entity.Cart;
import java.util.UUID;

public interface CartService {
    Cart getCartByUserId(UUID userId);
    void addToCart(UUID userId, UUID productId, int quantity);
    void removeFromCart(UUID userId, UUID productId);
    void clearCart(UUID userId);
}
