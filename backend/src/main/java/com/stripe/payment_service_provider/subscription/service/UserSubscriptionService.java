package com.stripe.payment_service_provider.subscription.service;

import com.stripe.payment_service_provider.payment.consumer.dto.SubscriptionItemDTO;
import com.stripe.payment_service_provider.payment.consumer.model.StripeCustomer;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.consumer.dto.SubscriptionDTO;

import java.util.Optional;
import java.util.Set;

public interface UserSubscriptionService {
    UserSubscription create(StripeCustomer stripeCustomer, SubscriptionDTO subscriptionDTO, SubscriptionItemDTO subscriptionItemDTO);
    UserSubscription update(SubscriptionDTO subscriptionDTO, SubscriptionItemDTO subscriptionItemDTO);
    Optional<UserSubscription> getActiveUserSubscription(Set<UserSubscription> userSubscriptions);
    UserSubscription getSubscriptionBySubscriptionId(String stripeCustomerId);
}
