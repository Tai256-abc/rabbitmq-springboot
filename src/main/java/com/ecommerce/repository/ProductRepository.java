
package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Modifying
    @Query(value = """
        UPDATE products
        SET stock_quantity = stock_quantity - :quantity
        WHERE id = :productId
          AND stock_quantity >= :quantity
        """, nativeQuery = true)
    int decreaseStockIfAvailable(
            @Param("productId") Long productId,
            @Param("quantity") int quantity
    );
}
