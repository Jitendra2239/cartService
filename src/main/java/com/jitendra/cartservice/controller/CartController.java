package com.jitendra.cartservice.controller;



import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;
import com.jitendra.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable Long userId) {

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addItem(
            @PathVariable Long userId,
            @RequestBody CartItem item) throws ExecutionException, InterruptedException {

        return ResponseEntity.ok(
                cartService.addItem(userId, item));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Cart> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                cartService.removeItem(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(
            @PathVariable Long userId) {

        cartService.clearCart(userId);

        return ResponseEntity.ok("Cart cleared");
    }
}