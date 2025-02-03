package com.project.po3d.controller;

import com.project.po3d.business.abstracts.CartService;
import com.project.po3d.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestParam UUID userId, @RequestParam UUID productId, @RequestParam int quantity) {
        cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok("Ürün sepete eklendi");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam UUID userId, @RequestParam UUID productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok("Ürün sepetten çıkarıldı");
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Sepet temizlendi");
    }
}
