package com.stripe.payment_service_provider.settings.exceptions.subscription;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SubscriptionException extends RuntimeException {

    private final HttpStatus status;
    private final String title;

    public SubscriptionException(String title, String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.title = title;
    }
}