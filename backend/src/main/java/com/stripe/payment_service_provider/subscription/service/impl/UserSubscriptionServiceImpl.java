package com.stripe.payment_service_provider.subscription.service.impl;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.service.UserSubscriptionService;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.dto.SubscriptionDTO;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.SubscriptionRepository;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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

    public UserSubscription getSubscriptionBySubscriptionId(String stripeSubscriptionId) throws ResourceNotFoundException {
        return subscriptionRepository.findUserSubscriptionsByStripeSubscriptionId(stripeSubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found " +  stripeSubscriptionId));
    }

    @Override
    public Optional<UserSubscription> getActiveUserSubscription(Set<UserSubscription> userSubscriptions) {
        return userSubscriptions.stream()
                .filter(s -> ACTIVE_STATUSES.contains(SubscriptionStatus.valueOf(s.getStatus().name().toUpperCase())))
                .findFirst();
    }

    @Override
    @Transactional
    public UserSubscription createOrUpdate(StripeCustomer stripeCustomer, SubscriptionDTO subscriptionDTO, StripePlan stripePlan) {
       UserSubscription userSubscription = subscriptionRepository.findUserSubscriptionsByStripeCustomer_StripeCustomerId(stripeCustomer.getStripeCustomerId())
                .orElseGet(() -> UserSubscription.builder().stripeCustomer(stripeCustomer).build());

        userSubscription.setStripePlan(stripePlan);
        userSubscription.setStatus(SubscriptionStatus.valueOf(subscriptionDTO.getStatus().name().toUpperCase()));
        userSubscription.setStripeSubscriptionId(subscriptionDTO.getStripeSubscriptionId());
        userSubscription.setCurrentPeriodStart(subscriptionDTO.getCurrentPeriodStart());
        userSubscription.setCurrentPeriodEnd(subscriptionDTO.getCurrentPeriodEnd());
        //subscription.getPlan().setTkResetHoursInterval(request.getTokenResetMinutesInterval());
        return subscriptionRepository.save(userSubscription);
    }
}