package com.stripe.payment_service_provider.payment.api.repository;

import com.stripe.payment_service_provider.payment.api.model.StripeInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<StripeInvoice, String> {
    Optional<List<StripeInvoice>> findStripeInvoiceBySubscription_Customer_StripeCustomerId(String customerId);
    Optional<StripeInvoice> findStripeInvoiceByInvoiceId(String invoiceId);
}
