package com.stripe.payment_service_provider.payment.consumer.dto;

import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;

import java.time.Instant;

public record SubscriptionDTO (
    String subscriptionId,
    SubscriptionStatus status,
    Instant currentPeriodStart,
    Instant currentPeriodEnd
){}
