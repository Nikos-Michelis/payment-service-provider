package com.stripe.payment_service_provider.settings.exceptions.common;

import com.stripe.exception.StripeException;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
