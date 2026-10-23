
package com.ecommerce.consumer;

import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info(
                "Received order-created event: orderId={}, userId={}, totalAmount={}",
                event.orderId(),
                event.userId(),
                event.totalAmount()
        );
    }
}
