package com.stripe.payment_service_provider.subscription.service.impl;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.model.SubscriptionUsage;
import com.stripe.payment_service_provider.subscription.repository.SubscriptionUsageRepository;
import com.stripe.payment_service_provider.subscription.service.SubscriptionUsageService;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static java.time.ZoneOffset.UTC;

@Service
public class SubscriptionUsageServiceImpl implements SubscriptionUsageService {

    private final SubscriptionUsageRepository subscriptionUsageRepository;

    public SubscriptionUsageServiceImpl(SubscriptionUsageRepository subscriptionUsageRepository) {
        this.subscriptionUsageRepository = subscriptionUsageRepository;
    }

    public void useFromLimit(UserSubscription userSubscription, String expenseId, int usage) {
        SubscriptionUsage subscriptionUsageEntity = SubscriptionUsage.builder()
                .userSubscription(userSubscription)
                .expenseId(expenseId)
                .usageCount(usage)
                .usageDate(Instant.now())
                .build();
        subscriptionUsageRepository.save(subscriptionUsageEntity);
    }

    public long getUsageByInterval(UserSubscription userSubscription, Instant now) {
        Instant startDate = now.atZone(UTC).minusHours(userSubscription.getStripePlan().getTkResetHoursInterval()).toInstant();
        Long usage = subscriptionUsageRepository.sumUsageBySubscriptionAndDateRange(userSubscription, startDate, now);
        return (usage == null) ? 0 : usage;
    }
}