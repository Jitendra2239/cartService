package com.jitendra.cartservice.service;


import com.jitendra.cartservice.dto.CartDto;
import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {

    CartDto getCart(Long userId);

    CartDto addItem(Long userId, CartItem item) throws ExecutionException, InterruptedException;

    CartDto removeItem(Long userId, Long productId);

    CartDto clearCart(Long userId);
}