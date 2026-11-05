
package com.ecommerce.controller;

import com.ecommerce.repository.OrderRevenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final OrderRevenueRepository revenueRepository;

    @GetMapping
    public Map<String, Object> getStatistics() {
        return Map.of(
            "totalOrders", revenueRepository.count(),
            "totalRevenue", revenueRepository.sumTotalAmount()
        );
    }
}
