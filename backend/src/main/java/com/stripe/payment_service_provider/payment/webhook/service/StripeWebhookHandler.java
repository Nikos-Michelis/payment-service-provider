package com.stripe.payment_service_provider.payment.webhook.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface StripeWebhookHandler {
    void saveEvent(Event event) throws StripeException;
}
