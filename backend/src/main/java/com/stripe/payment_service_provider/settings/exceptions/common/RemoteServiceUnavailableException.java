package com.stripe.payment_service_provider.settings.exceptions.common;

public class RemoteServiceUnavailableException extends RuntimeException{
    public RemoteServiceUnavailableException(String message) {
        super(message);
    }
    public RemoteServiceUnavailableException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
