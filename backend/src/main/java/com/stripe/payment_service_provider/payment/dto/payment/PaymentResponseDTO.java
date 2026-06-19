package com.stripe.payment_service_provider.payment.dto.payment;

import com.stripe.payment_service_provider.payment.model.InvoiceStatus;
import com.stripe.payment_service_provider.subscription.model.BillingCycle;

import java.math.BigDecimal;

public record PaymentResponseDTO(
    String sessionId,
    String email,
    String name,
    BillingCycle billingCycle,
    BigDecimal amountPaid,
    String currency,
    InvoiceStatus status
){}
