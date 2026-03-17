package com.jitendra.cartservice.config;

import com.jitendra.event.AddToCartEvent;
import com.jitendra.event.AddToCartResponseEvent;
import com.jitendra.event.InventoryCreatedEvent;
import com.jitendra.event.ProductCreatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Configuration
public class KafkaConfig {
    @Bean
    public ConcurrentMessageListenerContainer<String, AddToCartResponseEvent> replyContainer(
            ConsumerFactory<String, AddToCartResponseEvent> consumerFactory) {

        ContainerProperties containerProperties =
                new ContainerProperties("add-to-cart-response");

        return new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
    }
    @Bean
    public ReplyingKafkaTemplate<String, AddToCartEvent, AddToCartResponseEvent> replyingKafkaTemplate(
            ProducerFactory<String, AddToCartEvent> producerFactory,
            ConcurrentMessageListenerContainer<String, AddToCartResponseEvent> replyContainer) {

        ReplyingKafkaTemplate<String, AddToCartEvent, AddToCartResponseEvent> template =
                new ReplyingKafkaTemplate<>(producerFactory, replyContainer);

// 🔥 important
        return template;
    }
}

