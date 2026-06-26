package com.stripe.payment_service_provider.payment.repository;

import com.stripe.Stripe;
import com.stripe.payment_service_provider.payment.model.StripePaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<StripePaymentMethod, Long> {
    Optional<StripePaymentMethod> findPaymentMethodByStripePaymentMethodId(String paymentMethodId);
    @Query("""
        SELECT pm
        FROM StripePaymentMethod pm
        INNER JOIN pm.customer c
        WHERE pm.fingerprint = :fingerprint AND c.customerId = :customerId
    """)
    Optional<StripePaymentMethod> findStripePaymentMethodByPaymentMethodIdAndCustomerId(@Param("fingerprint") String fingerprint, @Param("customerId") Long customerId);
    List<StripePaymentMethod> findAllByCustomer_CustomerId(Long customerId);
}
