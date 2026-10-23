
package com.ecommerce.event;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        double totalAmount,
        String shippingAddress,
        String paymentMethod
) {
}
