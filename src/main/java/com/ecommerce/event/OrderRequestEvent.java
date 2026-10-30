
package com.ecommerce.event;

public record OrderRequestEvent(
        String requestId,
        Long userId,
        String shippingAddress,
        String paymentMethod
) {}
