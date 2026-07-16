package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findOrderLineByUuidAndOrder_Id(UUID uuid, long cartId);
    @Query("""
        SELECT ol
        FROM OrderItem ol
        INNER JOIN ol.order c
        INNER JOIN ol.product p
        WHERE c.id = :cartId AND p.id = :productId
    """)
    Optional<OrderItem> findOrderLineByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
}
