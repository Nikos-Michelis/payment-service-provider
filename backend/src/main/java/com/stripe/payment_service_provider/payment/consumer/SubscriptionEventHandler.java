package com.stripe.payment_service_provider.payment.consumer;

import com.stripe.model.Event;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.exception.StripeException;

public interface SubscriptionEventHandler {
    void onSubscriptionCreate(Event event) throws StripeException;
    void onSubscriptionUpdate(Event event) throws StripeException;
    void onSubscriptionCancel(Event event);
}
