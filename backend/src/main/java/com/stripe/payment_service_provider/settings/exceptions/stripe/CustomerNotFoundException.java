package com.stripe.payment_service_provider.settings.exceptions.stripe;

import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends StripeException {

    public CustomerNotFoundException(String message) {
        super("Stripe Customer Not Found, ", message, HttpStatus.NOT_FOUND);
    }
}
