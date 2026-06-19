package com.stripe.payment_service_provider.settings.exceptions.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException{
    public ConflictException(String message) { super(message); }
}
