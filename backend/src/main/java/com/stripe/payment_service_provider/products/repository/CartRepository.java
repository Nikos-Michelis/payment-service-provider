package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findCartByUser_id(Long userId);
}
