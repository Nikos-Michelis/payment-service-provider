package com.stripe.payment_service_provider.subscription.service.impl;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.service.UserSubscriptionService;
import com.stripe.payment_service_provider.settings.exceptions.subscription.SubscriptionNotFoundException;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.dto.StripeSubscriptionDTO;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final EnumSet<SubscriptionStatus> ACTIVE_STATUSES =
            EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING);
    private final SubscriptionRepository subscriptionRepository;

    public UserSubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public UserSubscription getSubscriptionBySubscriptionId(String stripeSubscriptionId) throws SubscriptionNotFoundException {
        return subscriptionRepository.findUserSubscriptionsByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found " +  stripeSubscriptionId));
    }

    @Override
    public Optional<UserSubscription> getActiveUserSubscription(Set<UserSubscription> userSubscriptions) {
        return userSubscriptions.stream()
                .filter(s -> ACTIVE_STATUSES.contains(SubscriptionStatus.valueOf(s.getStatus().name().toUpperCase())))
                .findFirst();
    }

    @Override
    @Transactional
    public UserSubscription createOrUpdate(StripeCustomer stripeCustomer, StripeSubscriptionDTO stripeSubscriptionDTO, StripePlan stripePlan) {
       UserSubscription userSubscription = subscriptionRepository.findUserSubscriptionsByStripeCustomer_StripeCustomerId(stripeCustomer.getStripeCustomerId())
                .orElseGet(() -> UserSubscription.builder().stripeCustomer(stripeCustomer).build());

        userSubscription.setStripePlan(stripePlan);
        userSubscription.setStatus(SubscriptionStatus.valueOf(stripeSubscriptionDTO.getStatus().name().toUpperCase()));
        userSubscription.setStripeSubscriptionId(stripeSubscriptionDTO.getStripeSubscriptionId());
        userSubscription.setCurrentPeriodStart(stripeSubscriptionDTO.getCurrentPeriodStart());
        userSubscription.setCurrentPeriodEnd(stripeSubscriptionDTO.getCurrentPeriodEnd());
        //subscription.getPlan().setTkResetHoursInterval(request.getTokenResetMinutesInterval());
        return subscriptionRepository.save(userSubscription);
    }
}