
package com.ecommerce.consumer;

import com.ecommerce.config.RabbitMQConfig;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.model.OrderRevenue;
import com.ecommerce.repository.OrderRevenueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticsConsumer {

    private final OrderRevenueRepository revenueRepository;

    @RabbitListener(queues = RabbitMQConfig.STATISTICS_QUEUE)
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {

        // Tranh ghi nhan trung mot don hang
        if (revenueRepository.existsByOrderId(event.orderId())) {
            log.info(
                "Order {} already counted; skipping duplicate",
                event.orderId()
            );
            return;
        }

        // Luu thong tin don hang de thong ke
        OrderRevenue revenue = new OrderRevenue(
            event.orderId(),
            event.userId(),
            event.totalAmount()
        );

        revenueRepository.save(revenue);

        // Tinh tong so don va tong gia tri don hang da ghi nhan
        Double totalRevenue = revenueRepository.sumTotalAmount();
        long totalOrders = revenueRepository.count();

        log.info(
            "Revenue statistics updated: orderId={}, orderAmount={}, totalOrders={}, totalRevenue={}",
            event.orderId(),
            event.totalAmount(),
            totalOrders,
            totalRevenue
        );
    }
}
