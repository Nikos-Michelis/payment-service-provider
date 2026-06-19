package com.stripe.payment_service_provider.settings.exceptions.subscription;

import org.springframework.http.HttpStatus;

public class SubscriptionNotFoundException extends SubscriptionException {

    public SubscriptionNotFoundException(String message) {
        super("Subscription Not Found, ", message, HttpStatus.NOT_FOUND);
    }
}
