
package com.ecommerce.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderRequestStatusStore {

    public record RequestStatus(
            String requestId,
            String status,
            String message,
            Long orderId
    ) {}

    private final Map<String, RequestStatus> statuses =
            new ConcurrentHashMap<>();

    public void markProcessing(String requestId) {
        statuses.put(requestId, new RequestStatus(
                requestId,
                "PROCESSING",
                "Yêu cầu đang được xử lý",
                null
        ));
    }

    public void markSuccess(String requestId, Long orderId) {
        statuses.put(requestId, new RequestStatus(
                requestId,
                "SUCCESS",
                "Đặt hàng thành công",
                orderId
        ));
    }

    public void markFailed(String requestId, String message) {
        statuses.put(requestId, new RequestStatus(
                requestId,
                "FAILED",
                message,
                null
        ));
    }

    public RequestStatus getStatus(String requestId) {
        return statuses.get(requestId);
    }
}
