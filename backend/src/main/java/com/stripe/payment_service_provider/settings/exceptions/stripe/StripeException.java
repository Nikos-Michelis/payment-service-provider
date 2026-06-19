package com.stripe.payment_service_provider.settings.exceptions.stripe;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StripeException extends RuntimeException {
    private final HttpStatus status;
    private final String title;

    public StripeException(String title, String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.title = title;
    }
}
