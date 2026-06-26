package com.stripe.payment_service_provider.subscription.repository;

import com.stripe.payment_service_provider.subscription.model.StripePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<StripePrice, Long> {
    Optional<StripePrice> findStripePriceByStripePriceId(String stripePriceId);
}
