
package com.ecommerce.service;

import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.event.OrderRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderRequestProducer {

    private final RabbitTemplate rabbitTemplate;
    private final OrderRequestStatusStore statusStore;

    public void publishOrderRequest(OrderRequestEvent event) {
        statusStore.markProcessing(event.requestId());

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_REQUEST_QUEUE,
                    event
            );
        } catch (Exception e) {
            statusStore.markFailed(
                    event.requestId(),
                    "Không thể gửi yêu cầu đến RabbitMQ"
            );
            throw e;
        }
    }
}
