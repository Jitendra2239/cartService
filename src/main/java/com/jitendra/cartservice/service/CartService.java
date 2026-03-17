package com.jitendra.cartservice.service;


import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {

    Cart getCart(Long userId);

    Cart addItem(Long userId, CartItem item) throws ExecutionException, InterruptedException;

    Cart removeItem(Long userId, Long productId);

    Cart clearCart(Long userId);
}