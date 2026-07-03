package com.stripe.payment_service_provider.subscription.service.impl;

import com.stripe.payment_service_provider.payment.api.dto.SubscriptionItemDTO;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.service.UserSubscriptionService;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.api.dto.SubscriptionDTO;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    private final EnumSet<SubscriptionStatus> ACTIVE_STATUSES =
            EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING);


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
    public UserSubscription create(StripeCustomer stripeCustomer, SubscriptionDTO subscriptionDTO, SubscriptionItemDTO subscriptionItemDTO) {
        String subscriptionId = subscriptionDTO.subscriptionId();

        UserSubscription userSubscription = UserSubscription.builder()
                .stripeSubscriptionId(subscriptionId)
                .customer(stripeCustomer)
                .stripePlan(subscriptionItemDTO.stripePlan())
                .stripePrice(subscriptionItemDTO.stripePrice())
                .status(SubscriptionStatus.valueOf(subscriptionDTO.status().name().toUpperCase()))
                .currentPeriodStart(subscriptionDTO.currentPeriodStart())
                .currentPeriodEnd(subscriptionDTO.currentPeriodEnd())
                .build();

        return subscriptionRepository.save(userSubscription);
    }

    @Override
    @Transactional
    public UserSubscription update(SubscriptionDTO subscriptionDTO, SubscriptionItemDTO subscriptionItemDTO) {
        String subscriptionId = subscriptionDTO.subscriptionId();
        UserSubscription userSubscription = subscriptionRepository.findUserSubscriptionsByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found " +  subscriptionId));

        userSubscription.setStripePlan(subscriptionItemDTO.stripePlan());
        userSubscription.setStripePrice(subscriptionItemDTO.stripePrice());
        userSubscription.setStatus(SubscriptionStatus.valueOf(subscriptionDTO.status().name().toUpperCase()));
        userSubscription.setCurrentPeriodStart(subscriptionDTO.currentPeriodStart());
        userSubscription.setCurrentPeriodEnd(subscriptionDTO.currentPeriodEnd());
        //subscription.getPlan().setTkResetHoursInterval(request.getTokenResetMinutesInterval());
        return subscriptionRepository.save(userSubscription);
    }

    public UserSubscription cancel(SubscriptionDTO subscriptionDTO) {
        String subscriptionId = subscriptionDTO.subscriptionId();

        UserSubscription userSubscription = subscriptionRepository.findUserSubscriptionsByStripeSubscriptionId(subscriptionId)
                .orElseThrow(() ->  new ResourceNotFoundException("Subscription not found " +  subscriptionId));

        userSubscription.setStatus(SubscriptionStatus.valueOf(subscriptionDTO.status().name().toUpperCase()));
        userSubscription.setCurrentPeriodEnd(subscriptionDTO.currentPeriodEnd());
        return subscriptionRepository.save(userSubscription);
    }

}