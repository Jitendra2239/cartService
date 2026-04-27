package com.jitendra.cartservice.service;

import com.jitendra.cartservice.dto.CartDto;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    public Cart toCart(CartDto dto) {

        if (dto == null) return null;

        Cart cart = new Cart();

        cart.setUserId(dto.getUserId());

        List<CartItem> items = dto.getItems() == null
                ? new ArrayList<>()
                : new ArrayList<>(dto.getItems());

        cart.setItems(items);

        double total = items.stream()
                .filter(Objects::nonNull)
                .mapToDouble(i -> {
                    double price = i.getPrice() == null ? 0.0 : i.getPrice();
                    int qty = i.getQuantity() == null ? 0 : i.getQuantity();
                    return price * qty;
                })
                .sum();

        cart.setTotalAmount(total);

        return cart;
    }
    public  CartDto toCartDto(Cart cart) {

        if (cart == null) return null;

        CartDto dto = new CartDto();
        dto.setUserId(cart.getUserId());
        dto.setItems(cart.getItems());

        double total = cart.getItems() == null ? 0.0 :
                cart.getItems().stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum();

        dto.setTotalAmount(total);

        return dto;
    }
    @Override
    public CartDto getCart(Long userId) {
        System.out.println("Getting cart for userId: " + userId);
   Cart cart= cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
      return toCartDto(cart);
    }

    // 2️⃣ Add Item
    @Override
    public CartDto addItem(Long userId, CartItem item)
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
        Cart cart1= cartRepository.save(cart);
        return toCartDto(cart1);
    }


    @Override
    public CartDto removeItem(Long userId, Long productId) {
        CartDto cart = getCart(userId);

        boolean removed = cart.getItems().removeIf(item ->
                item.getProductId().equals(productId)
        );

        if (!removed) {
            throw new ItemNotFoundException("Item not found in cart: " + productId);
        }
        Cart cart1= toCart(cart);
        cart1.setUserId(userId);
        Cart cart2= cartRepository.save(cart1);
        return toCartDto(cart2);
    }

    // 4️⃣ Clear Cart
    @Override
    public CartDto clearCart(Long userId) {
        CartDto cart = getCart(userId);
        cart.getItems().clear();
       Cart cart1= toCart(cart);
       cart1.setUserId(userId);
        Cart cart2= cartRepository.save(cart1);
       return  toCartDto(cart1);
    }
}

//public CartItem toCartItem(CartItemDto dto) {
//
//    CartItem item = new CartItem();
//    item.setProductId(dto.getProductId());
//    item.setQuantity(dto.getQuantity());
//    item.setPrice(dto.getPrice());
//
//    return item;
//}


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