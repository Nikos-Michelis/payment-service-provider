package com.stripe.payment_service_provider.payment.consumer.impl;

import com.stripe.model.Event;
import com.stripe.model.SubscriptionItem;
import com.stripe.payment_service_provider.payment.api.dto.SubscriptionItemDTO;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.consumer.SubscriptionEventHandler;
import com.stripe.payment_service_provider.payment.api.repository.CustomerRepository;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.api.dto.SubscriptionDTO;
import com.stripe.payment_service_provider.payment.api.util.ProductUtil;
import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SubscriptionEventHandlerImpl implements SubscriptionEventHandler {
    private final CustomerRepository customerRepository;
    private final UserSubscriptionServiceImpl subscriptionService;
    private final PlanRepository planRepository;
    private final ProductUtil productUtil;

    @Transactional
    public void onCreate(Event event) {
        Subscription subscription = handleSubscriptionEvent(event);
        String customerId = subscription.getCustomer();
        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        SubscriptionDTO subscriptionDTO = buildSubscriptionDTO(subscription);
        SubscriptionItemDTO subscriptionItemDTO = buildSubscriptionItemDTO(subscription);
        subscriptionService.create(stripeCustomer, subscriptionDTO, subscriptionItemDTO);
    }

    @Transactional
    public void onUpdate(Event event) {
        Subscription subscription = handleSubscriptionEvent(event);

        SubscriptionDTO subscriptionDTO = buildSubscriptionDTO(subscription);
        SubscriptionItemDTO subscriptionItemDTO = buildSubscriptionItemDTO(subscription);
        subscriptionService.update(subscriptionDTO, subscriptionItemDTO);
    }

    @Transactional
    public UserSubscription onCancel(Event event) {
        Subscription subscription = handleSubscriptionEvent(event);
        boolean isCancelingAtPeriodEnd = subscription.getCancelAtPeriodEnd();

        SubscriptionStatus effectiveStatus = isCancelingAtPeriodEnd
                ? SubscriptionStatus.ACTIVE
                : SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase());

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO(
                subscription.getId(),
                effectiveStatus,
                Instant.ofEpochSecond(subscription.getStartDate()),
                Instant.ofEpochSecond(subscription.getEndedAt())
        );

        return subscriptionService.cancel(subscriptionDTO);
    }

    private SubscriptionDTO buildSubscriptionDTO(Subscription subscription) {
        SubscriptionItem subscriptionItem = productUtil.getSubscriptionItem(subscription);

        return new SubscriptionDTO(
                subscription.getId(),
                SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase()),
                Instant.ofEpochSecond(subscriptionItem.getCurrentPeriodStart()),
                Instant.ofEpochSecond(subscriptionItem.getCurrentPeriodEnd())
        );
    }

    private SubscriptionItemDTO buildSubscriptionItemDTO(Subscription subscription) {
        SubscriptionItem subscriptionItem = productUtil.getSubscriptionItem(subscription);

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(subscriptionItem.getPlan().getProduct())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        StripePrice stripePrice = productUtil.getPriceFromPlanAvailablePrices(stripePlan.getPrices(), subscriptionItem.getPrice().getId());

        return new SubscriptionItemDTO(stripePlan, stripePrice);
    }

    private Subscription handleSubscriptionEvent(Event event) {
        return (Subscription) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }
}
