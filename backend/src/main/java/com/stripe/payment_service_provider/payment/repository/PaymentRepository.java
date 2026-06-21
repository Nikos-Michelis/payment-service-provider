package com.stripe.payment_service_provider.payment.repository;

import com.stripe.payment_service_provider.payment.model.StripePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<StripePayment, Long> {
}
