package com.project.po3d.business.concretes;

import com.project.po3d.business.abstracts.CartService;
import com.project.po3d.entity.Cart;
import com.project.po3d.entity.CartItem;
import com.project.po3d.entity.User;
import com.project.po3d.repository.CartRepository;
import com.project.po3d.repository.CartItemRepository;
import com.project.po3d.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CartManager implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public CartManager(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart getCartByUserId(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.flatMap(cartRepository::findByUser).orElse(null);
    }

    @Override
    public void addToCart(UUID userId, UUID productId, int quantity) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Cart cart = cartRepository.findByUser(user.get()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user.get());
            return cartRepository.save(newCart);
        });

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProductId(productId);
        cartItem.setQuantity(quantity);
        
        cartItemRepository.save(cartItem);
    }

    @Override
    public void removeFromCart(UUID userId, UUID productId) {
        Optional<User> user = userRepository.findById(userId);
        user.flatMap(cartRepository::findByUser).ifPresent(cart -> {
            cart.getItems().removeIf(item -> item.getProductId().equals(productId));
            cartRepository.save(cart);
        });
    }

    @Override
    public void clearCart(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        user.flatMap(cartRepository::findByUser).ifPresent(cart -> {
            cartItemRepository.deleteAll(cart.getItems());
        });
    }
}
