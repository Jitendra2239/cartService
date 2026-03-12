package com.jitendra.cartservice.service;

import com.jitendra.cartservice.exception.CartNotFoundException;
import com.jitendra.cartservice.exception.ItemNotFoundException;
import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;
import com.jitendra.cartservice.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new CartNotFoundException(
                    "Cart not found for user " + userId);
        }

        return cart;
    }

    @Override
    public Cart addItem(Long userId, CartItem item) {

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setItems(new ArrayList<>());
        }

        List<CartItem> items = cart.getItems();

        items.add(item);

        cart.setItems(items);

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeItem(Long userId, Long productId) {

        Cart cart = getCart(userId);

        boolean removed = cart.getItems()
                .removeIf(i -> i.getProductId().equals(productId));

        if (!removed) {
            throw new ItemNotFoundException("Product not found in cart");
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new CartNotFoundException(
                    "Cart not found for user " + userId);
        }

        cartRepository.delete(userId);
        return cart;
    }
}