package com.jitendra.cartservice.service;

import com.jitendra.cartservice.exception.CartNotFoundException;
import com.jitendra.cartservice.exception.ItemNotFoundException;
import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;
import com.jitendra.cartservice.repository.CartRepository;

import com.jitendra.cartservice.visitor.PriceCalculatorVisitor;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    // 1️⃣ Get Cart
    @Override
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
    }

    // 2️⃣ Add Item
    @Override
    public Cart addItem(Long userId, CartItem item)
            throws ExecutionException, InterruptedException {


        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setItems(new ArrayList<>());
                    return newCart;
                });


        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(item.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Increase quantity
            CartItem existing = existingItem.get();
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        } else {
            // Add new item
            cart.getItems().add(item);
        }
        PriceCalculatorVisitor priceCalculatorVisitor=new PriceCalculatorVisitor();
        cart.setTotalAmount(priceCalculatorVisitor.getTotal());
        return cartRepository.save(cart);
    }


    @Override
    public Cart removeItem(Long userId, Long productId) {
        Cart cart = getCart(userId);

        boolean removed = cart.getItems().removeIf(item ->
                item.getProductId().equals(productId)
        );

        if (!removed) {
            throw new ItemNotFoundException("Item not found in cart: " + productId);
        }

        return cartRepository.save(cart);
    }

    // 4️⃣ Clear Cart
    @Override
    public Cart clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}

//    @KafkaListener(topics = "add-to-cart-response", groupId = "cart-group")
//    public void handleAddToCartResponse(AddToCartResponseEvent response) {
//
//        if (!response.isSuccess()) {
//            System.out.println("Add to cart failed: " + response.getMessage());
//            cartRepository.delete(response.getUserId());
//            return;
//        }
//
//        Cart cart = cartRepository.findByUserId(response.getUserId());
//
//        if (cart == null) {
//            cart = new Cart();
//            cart.setUserId(response.getUserId());
//            cart.setItems(new ArrayList<>());
//        }
//
//        Optional<CartItem> existing = cart.getItems().stream()
//                .filter(i -> i.getProductId().equals(response.getProductId()))
//                .findFirst();
//
//        if (existing.isPresent()) {
//            existing.get().setQuantity(existing.get().getQuantity() + 1);
//
//        } else {
//            CartItem item = new CartItem();
//            item.setProductId(response.getProductId());
//            item.setProductName(response.getProductName());
//            item.setPrice(response.getPrice());
//            item.setQuantity(1);
//
//
//            cart.getItems().add(item);
//        }
//
//        cartRepository.save(cart);
//    }
//}