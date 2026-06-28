package com.stripe.payment_service_provider.payment.consumer.repository;

import com.stripe.payment_service_provider.payment.consumer.dto.payment.response.PaymentResponseDTO;
import com.stripe.payment_service_provider.payment.consumer.model.CustomerPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerPortalRepository extends JpaRepository<CustomerPortal, Long> {

    Optional<CustomerPortal> findCustomerPortalByIdempotencyKey(String idempotencyKey);

    @Query("""
        SELECT new com.stripe.payment_service_provider.payment.consumer.dto.payment.response.PaymentResponseDTO(
            cp.sessionId,
            c.email,
            sp.name,
            spc.billingCycle,
            si.amountPaid,
            si.currency,
            si.status
        )
        FROM CustomerPortal cp
        INNER JOIN cp.customer c
        INNER JOIN c.subscriptions s
        INNER JOIN s.stripePlan sp
        INNER JOIN s.stripePrice spc
        INNER JOIN s.stripeInvoices si
        WHERE cp.sessionId = :sessionId
        ORDER BY cp.createdAt DESC
        LIMIT 1
    """)
    Optional<PaymentResponseDTO> findCustomerPortalById(@Param("sessionId") String sessionId);
}
