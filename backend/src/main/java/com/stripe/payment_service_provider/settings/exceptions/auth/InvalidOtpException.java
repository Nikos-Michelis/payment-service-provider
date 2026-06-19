package com.stripe.payment_service_provider.settings.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidOtpException extends RuntimeException{
    public InvalidOtpException(String message) {
        super(message);
    }
}
