package com.stripe.payment_service_provider.payment.api.repository;

import com.stripe.payment_service_provider.payment.api.model.StripePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<StripePayment, Long> {
    Optional<StripePayment> findStripePaymentByPaymentIntentId(String paymentIntentId);
}
