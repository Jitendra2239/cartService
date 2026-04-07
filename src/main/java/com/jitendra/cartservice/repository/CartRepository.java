package com.jitendra.cartservice.repository;


import com.jitendra.cartservice.model.Cart;

import java.util.Optional;

public interface CartRepository {

    Cart save(Cart cart);

    Optional<Cart> findByUserId(Long userId);

    void delete(Long userId);
}