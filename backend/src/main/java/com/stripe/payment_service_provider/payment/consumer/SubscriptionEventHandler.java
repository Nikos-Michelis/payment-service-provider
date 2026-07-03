package com.stripe.payment_service_provider.payment.consumer;

import com.stripe.model.Event;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.exception.StripeException;

public interface SubscriptionEventHandler {
    void onCreate(Event event) throws StripeException;
    void onUpdate(Event event) throws StripeException;
    UserSubscription onCancel(Event event);
}
