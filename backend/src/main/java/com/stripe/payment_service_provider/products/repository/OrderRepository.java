package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
