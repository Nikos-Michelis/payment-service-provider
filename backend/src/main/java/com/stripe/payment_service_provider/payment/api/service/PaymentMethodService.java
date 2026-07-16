package com.stripe.payment_service_provider.payment.api.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface PaymentMethodService {
    void addPaymentMethod(Event event) throws StripeException;
    void removePaymentMethod(Event event) throws StripeException;
}
