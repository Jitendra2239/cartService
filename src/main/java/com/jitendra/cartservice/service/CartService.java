package com.jitendra.cartservice.service;


import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;

public interface CartService {

    Cart getCart(Long userId);

    Cart addItem(Long userId, CartItem item);

    Cart removeItem(Long userId, Long productId);

    Cart clearCart(Long userId);
}