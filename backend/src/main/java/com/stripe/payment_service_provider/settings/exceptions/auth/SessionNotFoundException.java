package com.stripe.payment_service_provider.settings.exceptions.auth;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
