package com.jitendra.cartservice.repository;


import com.jitendra.cartservice.model.Cart;

public interface CartRepository {

    Cart save(Cart cart);

    Cart findByUserId(Long userId);

    void delete(Long userId);
}