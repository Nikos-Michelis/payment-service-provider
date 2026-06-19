package com.stripe.payment_service_provider.payment.repository;

import com.stripe.payment_service_provider.payment.dto.payment.PaymentResponseDTO;
import com.stripe.payment_service_provider.payment.model.CustomerPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerPortalRepository extends JpaRepository<CustomerPortal, Long> {

    Optional<CustomerPortal> findCustomerPortalByIdempotencyKey(String idempotencyKey);

    @Query("""
        SELECT new com.stripe.payment_service_provider.payment.dto.payment.PaymentResponseDTO(
            cp.sessionId,
            c.email,
            sp.name,
            sp.billingCycle,
            si.amountPaid,
            si.currency,
            si.status
        )
        FROM CustomerPortal cp
        INNER JOIN cp.customer c
        INNER JOIN c.subscriptions s
        INNER JOIN s.stripePlan sp
        INNER JOIN s.stripeInvoices si
        WHERE cp.sessionId = :sessionId
        ORDER BY cp.createdAt DESC
        LIMIT 1
    """)
    Optional<PaymentResponseDTO> findCustomerPortalById(@Param("sessionId") String sessionId);
}
