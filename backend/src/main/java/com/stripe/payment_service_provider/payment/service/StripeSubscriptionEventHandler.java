package com.stripe.payment_service_provider.payment.service;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;

public interface StripeSubscriptionEventHandler {
    void handleSubscriptionChange(Subscription subscription) throws StripeException;
    UserSubscription handleSubscriptionCancellation(Subscription subscription);
}
