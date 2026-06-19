package com.stripe.payment_service_provider.subscription.repository;

import com.stripe.payment_service_provider.subscription.model.StripePrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceRepository extends JpaRepository<StripePrice, Long> {
    Optional<StripePrice> findStripePriceByStripePriceId(String stripePriceId);
}
