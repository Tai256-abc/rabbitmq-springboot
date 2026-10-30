
package com.ecommerce.repository;

import com.ecommerce.model.OrderRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRevenueRepository
        extends JpaRepository<OrderRevenue, Long> {

    boolean existsByOrderId(Long orderId);

    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM OrderRevenue r")
    Double sumTotalAmount();
}
