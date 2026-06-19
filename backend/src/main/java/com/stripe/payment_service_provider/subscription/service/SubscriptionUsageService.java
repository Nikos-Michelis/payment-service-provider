package com.stripe.payment_service_provider.subscription.service;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;

import java.time.Instant;

public interface SubscriptionUsageService {
    void useFromLimit(UserSubscription userSubscription, String expenseId, int usage);
    long getUsageByInterval(UserSubscription userSubscription, Instant now);
}
