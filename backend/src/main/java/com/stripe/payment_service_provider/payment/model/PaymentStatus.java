package com.stripe.payment_service_provider.payment.model;

public enum PaymentStatus {
    PENDING,
    OPEN,
    PAID,
    CANCELED,
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_CONFIRMATION,
    REQUIRES_ACTION,
    PROCESSING,
    REQUIRES_CAPTURE,
    SUCCEEDED
}
