package com.stripe.payment_service_provider.payment.producer.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface StripeWebhookHandler {
    void handleStripeEvent(Event event) throws StripeException;
}
