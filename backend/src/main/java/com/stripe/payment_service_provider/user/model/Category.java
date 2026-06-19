package com.stripe.payment_service_provider.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Category {
    BUG("BUG"),
    ACCOUNT("ACCOUNT"),
    OTHER("OTHER");

    private final String value;

}
