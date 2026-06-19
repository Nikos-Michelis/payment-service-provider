package com.stripe.payment_service_provider.subscription.model;

import lombok.Getter;

@Getter
public enum SubscriptionStatus {
    INCOMPLETE("INCOMPLETE"),
    INCOMPLETE_EXPIRED("INCOMPLETE_EXPIRED"),
    TRIALING("TRIALING"),
    ACTIVE("ACTIVE"),
    PAST_DUE("PAST_DUE"),
    CANCELED("CANCELED"),
    UNPAID("UNPAID"),
    PAUSED("PAUSED");

    private final String value;

    SubscriptionStatus(String value) {
        this.value = value;
    }
}