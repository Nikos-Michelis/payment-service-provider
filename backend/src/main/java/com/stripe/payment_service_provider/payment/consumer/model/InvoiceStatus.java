package com.stripe.payment_service_provider.payment.consumer.model;

import lombok.Getter;

@Getter
public enum InvoiceStatus {
    OPEN("OPEN", "Invoice is open and awaiting payment"),
    DRAFT("DRAFT", "Invoice is in draft state"),
    PAID("PAID", "Invoice has been paid"),
    UNCOLLECTIBLE("UNCOLLECTIBLE", "Invoice is marked as uncollectible"),
    VOID("VOID", "Invoice has been voided");
    
    private final String name;
    private final String description;
    
    InvoiceStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isPaid() {
        return this == PAID;
    }
    
    public boolean isCollectable() {
        return this == OPEN || this == DRAFT;
    }
    
    public boolean isFinal() {
        return this == PAID || this == UNCOLLECTIBLE || this == VOID;
    }
}