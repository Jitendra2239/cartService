package com.jitendra.cartservice.service;

import com.jitendra.cartservice.exception.CartNotFoundException;
import com.jitendra.cartservice.exception.ItemNotFoundException;
import com.jitendra.cartservice.model.Cart;
import com.jitendra.cartservice.model.CartItem;
import com.jitendra.cartservice.repository.CartRepository;
import com.jitendra.event.AddToCartEvent;
import com.jitendra.event.AddToCartResponseEvent;

import com.jitendra.event.InventoryCreatedEvent;
import com.jitendra.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private static final String PREFIX = "cart:";
    private final CartRepository cartRepository;

    private final ReplyingKafkaTemplate<String, AddToCartEvent, AddToCartResponseEvent> kafkaTemplate;


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

        // 1️⃣ Create Event
        AddToCartEvent event = new AddToCartEvent();
       // 🔥 important
        event.setUserId(userId);
        event.setProductId(item.getProductId());
        event.setQuantity(item.getQuantity());

        // 2️⃣ Send event to Kafka (fire-and-forget)
        kafkaTemplate.send("add-to-cart", event);

        // 3️⃣ OPTIONAL: mark cart as pending (good for UI)
        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setItems(new ArrayList<>());
        }

        // you can store pending item (optional)
        CartItem pendingItem = new CartItem();
        pendingItem.setProductId(item.getProductId());
        pendingItem.setQuantity(item.getQuantity());
         // 🔥 optional field
         pendingItem.setProductName(item.getProductName());
         pendingItem.setQuantity(item.getQuantity());
         pendingItem.setPrice(item.getPrice());


        cart.getItems().add(pendingItem);

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

    @KafkaListener(topics = "add-to-cart-response", groupId = "cart-group")
    public void handleAddToCartResponse(AddToCartResponseEvent response) {

        if (!response.isSuccess()) {
            System.out.println("Add to cart failed: " + response.getMessage());
            cartRepository.delete(response.getUserId());
            return;
        }

        Cart cart = cartRepository.findByUserId(response.getUserId());

        if (cart == null) {
            cart = new Cart();
            cart.setUserId(response.getUserId());
            cart.setItems(new ArrayList<>());
        }

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(response.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + 1);

        } else {
            CartItem item = new CartItem();
            item.setProductId(response.getProductId());
            item.setProductName(response.getProductName());
            item.setPrice(response.getPrice());
            item.setQuantity(1);


            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }
}