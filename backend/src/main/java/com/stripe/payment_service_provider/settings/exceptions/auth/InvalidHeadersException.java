package com.stripe.payment_service_provider.settings.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidHeadersException extends RuntimeException {
    public InvalidHeadersException(String message) {
        super(message);
    }

}
