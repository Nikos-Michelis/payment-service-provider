package com.stripe.payment_service_provider.payment.webhook.model;

public enum EventStatus {
    PENDING,
    PUBLISHED,
    PROCESSED,
    SKIPPED,
    FAILED
}
