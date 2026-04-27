package com.jitendra.cartservice.controller;



import com.jitendra.cartservice.dto.CartDto;
import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;
import com.jitendra.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

        import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {

        String user=authentication.getPrincipal().toString();
        System.out.println("user->"+user);


        return ResponseEntity.ok(cartService.getCart(Long.parseLong(user)));
    }

    @GetMapping("/internal/{userId}")
    public ResponseEntity<CartDto> getCart1(@PathVariable Long userId) {


        System.out.println("user->"+userId);


        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/add")
    public ResponseEntity<CartDto> addItem(
            @RequestBody CartItem item,
            Authentication authentication) throws ExecutionException, InterruptedException {

        Long userId =  Long.parseLong(authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                cartService.addItem(userId, item));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeItem(
            @PathVariable Long productId,
            Authentication authentication) {

        Long userId =  Long.parseLong(authentication.getPrincipal().toString());

        return ResponseEntity.ok(
                cartService.removeItem(userId, productId));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Authentication authentication) {

        Long userId =  Long.parseLong(authentication.getPrincipal().toString());

        cartService.clearCart(userId);

        return ResponseEntity.ok("Cart cleared");
    }
}