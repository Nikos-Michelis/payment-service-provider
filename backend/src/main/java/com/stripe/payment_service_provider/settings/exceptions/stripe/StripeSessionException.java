package com.stripe.payment_service_provider.settings.exceptions.stripe;

import org.springframework.http.HttpStatus;

public class StripeSessionException extends CustomStripeException {
    public StripeSessionException(String title, String message, HttpStatus status) {
        super(title, message, status);
    }
}
