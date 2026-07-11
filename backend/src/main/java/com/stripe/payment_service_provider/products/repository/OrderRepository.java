package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByUuid(UUID orderUUID);
}
