package com.stripe.payment_service_provider.subscription.service;

import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.dto.SubscriptionDTO;

import java.util.Optional;
import java.util.Set;

public interface UserSubscriptionService {
    UserSubscription createOrUpdate(StripeCustomer stripeCustomer, SubscriptionDTO subscriptionDTO, StripePlan stripePlan);
    Optional<UserSubscription> getActiveUserSubscription(Set<UserSubscription> userSubscriptions);
    UserSubscription getSubscriptionBySubscriptionId(String stripeCustomerId);
}
