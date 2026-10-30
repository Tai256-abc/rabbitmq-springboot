
package com.ecommerce.controller;

import com.ecommerce.dto.CreateOrderRequest;
import com.ecommerce.dto.UpdatePaymentRequest;
import com.ecommerce.event.OrderRequestEvent;
import com.ecommerce.model.Order;
import com.ecommerce.service.OrderRequestProducer;
import com.ecommerce.service.OrderRequestStatusStore;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderService orderService;
    private final OrderRequestProducer orderRequestProducer;
    private final OrderRequestStatusStore statusStore;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(
                orderService.getOrdersByUserId(userId)
        );
    }

    // Đặt hàng trực tiếp, không qua RabbitMQ
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(
                        request.getUserId(),
                        request.getShippingAddress(),
                        request.getPaymentMethod()
                ));
    }

    // Gửi yêu cầu đặt hàng qua RabbitMQ
    @PostMapping("/queue")
    public ResponseEntity<?> queueOrder(
            @RequestBody CreateOrderRequest request) {

        String requestId = UUID.randomUUID().toString();

        OrderRequestEvent event = new OrderRequestEvent(
                requestId,
                request.getUserId(),
                request.getShippingAddress(),
                request.getPaymentMethod()
        );

        // Khởi tạo trạng thái trước khi gửi message
        statusStore.markProcessing(requestId);

        try {
            orderRequestProducer.publishOrderRequest(event);

            return ResponseEntity.accepted().body(
                    Map.of(
                            "requestId", requestId,
                            "status", "PROCESSING",
                            "message", "Yeu cau dat hang da duoc tiep nhan"
                    )
            );
        } catch (Exception e) {
            statusStore.markFailed(
                    requestId,
                    e.getMessage() == null
                            ? "Khong the gui yeu cau vao RabbitMQ"
                            : e.getMessage()
            );

            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR
            ).body(
                    Map.of(
                            "requestId", requestId,
                            "status", "FAILED",
                            "message", "Khong the gui yeu cau vao RabbitMQ"
                    )
            );
        }
    }

    // Tra cứu trạng thái xử lý yêu cầu RabbitMQ
    @GetMapping("/queue/{requestId}")
    public ResponseEntity<?> getQueueOrderStatus(
            @PathVariable String requestId) {

        OrderRequestStatusStore.RequestStatus status =
                statusStore.getStatus(requestId);

        if (status == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "status", "NOT_FOUND",
                                    "message",
                                    "Khong tim thay yeu cau dat hang"
                            )
                    );
        }

        return ResponseEntity.ok(status);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(
                orderService.updateOrderStatus(id, status)
        );
    }

    @PutMapping("/{id}/payment")
    public ResponseEntity<Order> updatePayment(
            @PathVariable Long id,
            @RequestBody UpdatePaymentRequest request) {
        return ResponseEntity.ok(
                orderService.updatePaymentId(
                        id,
                        request.getPaymentId()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
