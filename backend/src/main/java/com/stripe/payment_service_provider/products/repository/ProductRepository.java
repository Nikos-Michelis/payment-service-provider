package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
