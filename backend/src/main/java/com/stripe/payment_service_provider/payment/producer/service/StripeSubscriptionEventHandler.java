package com.stripe.payment_service_provider.payment.producer.service;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

public interface StripeSubscriptionEventHandler {
    void handleSubscriptionCreate(Subscription subscription) throws StripeException;
    void handleSubscriptionUpdate(Subscription subscription) throws StripeException;
    UserSubscription handleSubscriptionCancel(Subscription subscription);
}
