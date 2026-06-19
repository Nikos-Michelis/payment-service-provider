package com.stripe.payment_service_provider.settings.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidGoogleJwtToken extends RuntimeException {
    public InvalidGoogleJwtToken (String message) { super(message); }
    public InvalidGoogleJwtToken (String message, Throwable throwable) { super(message, throwable); }
}
