package com.stripe.payment_service_provider.payment.util;

import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.SubscriptionListParams.Status;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.LineItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SubscriptionUtil {

    public Optional<Subscription> getSubscriptionByStatusAndCustomer(String customerId, EnumSet<SubscriptionStatus> statuses) throws StripeException {
        SubscriptionListParams params = buildSubscriptionListParams("ALL", 10L)
                .setCustomer(customerId)
                .build();

        return Subscription.list(params).getData()
                .stream()
                .filter(sub -> statuses.contains(SubscriptionStatus.valueOf(sub.getStatus().toUpperCase())))
                .findFirst();
    }

    public List<Subscription> getLatestSubscriptionPerCustomer() throws StripeException {
        SubscriptionListParams params = buildSubscriptionListParams("ALL", 10L).build();
        return Subscription.list(params).getData()
                .stream()
                .collect(Collectors.toMap(
                        Subscription::getCustomer, s -> s, (existing, newer) -> newer.getCreated() > existing.getCreated() ? newer : existing
                ))
                .values()
                .stream()
                .sorted((o1, o2) -> Long.compare(o2.getCreated(), o1.getCreated()))
                .toList();
    }

    public LineItem buildSubscriptionLineItem(Product product) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPrice(product.getDefaultPrice())
                .build();
    }

    public SubscriptionListParams.Builder buildSubscriptionListParams(String status, long limit) {
        return SubscriptionListParams.builder()
                .setStatus(Status.valueOf(status.toUpperCase()))
                .setLimit(limit);
    }

    public Long getSubscriptionItemPeriodEnd(Subscription subscription) {
        SubscriptionItemCollection items = subscription.getItems();
        if (items == null) {
            throw new RuntimeException("Subscription items not found.");
        }
        return items.getData().getFirst().getCurrentPeriodEnd();
    }

    public boolean isSubscriptionItemTrialEnds(Subscription subscription) {
        return Instant.ofEpochSecond(subscription.getTrialEnd()).isAfter(Instant.now());
    }

    public SubscriptionItemListParams getSubscriptionItemListParams(Subscription subscription) {
        return SubscriptionItemListParams.builder()
                .setSubscription(subscription.getId())
                .addExpand("data.price.product")
                .build();
    }

    public SubscriptionItemCollection getSubscriptionItemCollection(SubscriptionItemListParams subscriptionItemListParams) throws StripeException {
        return SubscriptionItem.list(subscriptionItemListParams);
    }
}
