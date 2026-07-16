package com.stripe.payment_service_provider.payment.consumer;

import com.stripe.model.Event;
import com.stripe.payment_service_provider.payment.api.model.PaymentStatus;

public interface PaymentIntentHandler {
    void onPaymentIntentCreate(Event event);
    void onPaymentIntentUpdate(Event event, PaymentStatus status);
}
