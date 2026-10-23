package com.ecommerce.service;

import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_CREATED_QUEUE,
                event
        );
    }
}