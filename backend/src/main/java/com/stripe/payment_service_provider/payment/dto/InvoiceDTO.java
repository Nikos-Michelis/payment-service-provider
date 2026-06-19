package com.stripe.payment_service_provider.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stripe.payment_service_provider.payment.model.BillingReason;
import com.stripe.payment_service_provider.payment.model.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceDTO {
    private String invoiceStripeId;
    private String subscriptionId;
    private BigDecimal amountPaid;
    private String currency;
    private InvoiceStatus status;
    private BillingReason billingReason;
    private String hostedInvoiceUrl;
    private Instant nextPaymentAttempt;
    private Instant invoiceCreatedAt;
    private Instant finalizedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
