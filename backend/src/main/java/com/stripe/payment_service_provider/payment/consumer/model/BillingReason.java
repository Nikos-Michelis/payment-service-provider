package com.stripe.payment_service_provider.payment.consumer.model;

import lombok.Getter;

@Getter
public enum BillingReason {
    MANUAL("MANUAL", "Manually created invoice"),
    SUBSCRIPTION_CREATE("SUBSCRIPTION CREATE", "Invoice created when subscription starts"),
    SUBSCRIPTION_CYCLE("SUBSCRIPTION CYCLE", "Regular subscription billing cycle"),
    SUBSCRIPTION_UPDATE("SUBSCRIPTION UPDATE", "Invoice due to subscription changes"),
    SUBSCRIPTION_THRESHOLD("SUBSCRIPTION THRESHOLD", "Invoice created due to billing threshold"),
    UPCOMING("UPCOMING", "Upcoming invoice preview");
    
    private final String name;
    private final String description;
    
    BillingReason(String name, String description) {
        this.name = name;
        this.description = description;
    }
}