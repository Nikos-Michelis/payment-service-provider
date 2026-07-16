package com.stripe.payment_service_provider.payment.api.model;

public enum PaymentStatus {
    CREATED,              // payment_intent.created
    PROCESSING,           // payment_intent.processing
    REQUIRES_ACTION,      // payment_intent.requires_action (3DS)
    CAPTURED,             // payment_intent.succeeded
    FAILED,               // payment_intent.payment_failed
    CANCELED,             // payment_intent.canceled
    DISPUTED,             // charge.dispute.created
    REFUND_PENDING,       // refund initiated
    PARTIALLY_REFUNDED,   // charge.refunded (partial)
    REFUNDED              // charge.refunded (full)
}