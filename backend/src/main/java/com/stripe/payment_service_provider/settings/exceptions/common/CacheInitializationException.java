package com.stripe.payment_service_provider.settings.exceptions.common;

public class CacheInitializationException extends RuntimeException {
    public CacheInitializationException(String message) { super(message); }
    public CacheInitializationException(String message, Throwable throwable) { super(message, throwable); }

}
