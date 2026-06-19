package com.stripe.payment_service_provider.settings.exceptions.stripe;

import org.springframework.http.HttpStatus;

public class PlanNotFoundException extends StripeException {

    public PlanNotFoundException(String message) {
        super("Plan Not Found, ", message, HttpStatus.NOT_FOUND);
    }
}
