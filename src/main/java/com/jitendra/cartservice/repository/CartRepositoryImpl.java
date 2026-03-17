package com.jitendra.cartservice.repository;



import com.jitendra.cartservice.model.Cart;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private static final String KEY = "cart:";

    private final RedisTemplate<String, Object> redisTemplate;

    public CartRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Cart save(Cart cart) {

        redisTemplate.opsForValue()
                .set(KEY + cart.getUserId(), cart, Duration.ofHours(24));

        return cart;
    }

    @Override
    public Cart findByUserId(Long userId) {

        return (Cart) redisTemplate.opsForValue()
                .get(KEY + userId);
    }

    @Override
    public void delete(Long userId) {

        redisTemplate.delete(KEY + userId);
    }
}