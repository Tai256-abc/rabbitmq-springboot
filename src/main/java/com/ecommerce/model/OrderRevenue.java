
package com.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "order_revenue",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_order_revenue_order_id",
            columnNames = "order_id"
        )
    }
)
public class OrderRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public OrderRevenue() {
    }

    public OrderRevenue(
            Long orderId,
            Long userId,
            Double totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.recordedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
