package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
    Optional<OrderLine> findOrderLineByProduct_Id(Long productId);
}
