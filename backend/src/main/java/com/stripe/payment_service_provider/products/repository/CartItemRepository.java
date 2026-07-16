package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.CartItem;
import com.stripe.payment_service_provider.products.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findOrderLineByUuidAndCart_Id(UUID uuid, long cartId);
    @Query("""
        SELECT ol
        FROM CartItem ol
        INNER JOIN ol.cart c
        INNER JOIN ol.product p
        WHERE c.id = :cartId AND p.id = :productId
    """)
    Optional<CartItem> findOrderLineByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
}
