
package com.ecommerce.service;

import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.event.OrderRequestEvent;
import com.ecommerce.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderRequestConsumer {

    private final OrderService orderService;
    private final OrderRequestStatusStore statusStore;

    @RabbitListener(queues = RabbitMQConfig.ORDER_REQUEST_QUEUE)
    public void handleOrderRequest(OrderRequestEvent event) {
        log.info(
                "Received order request: requestId={}, userId={}",
                event.requestId(),
                event.userId()
        );

        try {
            Order order = orderService.createOrder(
                    event.userId(),
                    event.shippingAddress(),
                    event.paymentMethod()
            );

            statusStore.markSuccess(
                    event.requestId(),
                    order.getId()
            );

            log.info(
                    "Order request succeeded: requestId={}, orderId={}",
                    event.requestId(),
                    order.getId()
            );

        } catch (Exception e) {
            String reason = e.getMessage() == null
                    ? "Lỗi không xác định khi xử lý đơn hàng"
                    : e.getMessage();

            statusStore.markFailed(event.requestId(), reason);

            log.error(
                    "Order request failed: requestId={}, reason={}",
                    event.requestId(),
                    reason
            );

            throw new AmqpRejectAndDontRequeueException(
                    "Cannot process order request",
                    e
            );
        }
    }
}
