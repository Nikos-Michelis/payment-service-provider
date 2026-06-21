package com.stripe.payment_service_provider.payment.service.impl.webhook;

import com.stripe.payment_service_provider.payment.mappers.StripeSubscriptionObjectMapper;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.dto.SubscriptionDTO;
import com.stripe.payment_service_provider.payment.service.StripeSubscriptionEventHandler;
import com.stripe.payment_service_provider.payment.util.ProductUtil;
import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SubscriptionEventHandlerImpl implements StripeSubscriptionEventHandler {
    private final UserRepository userRepository;
    private final UserSubscriptionServiceImpl subscriptionService;
    private final PlanRepository planRepository;
    private final ProductUtil productUtil;
    private final StripeSubscriptionObjectMapper subscriptionObjectMapper;

    @Transactional
    public void handleSubscriptionChange(Subscription subscription) throws StripeException {
        String customerId = subscription.getCustomer();
        Product product = productUtil.getProductBySubscription(subscription);

        User user = userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO(
                subscription.getId(),
                SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase()),
                Instant.ofEpochSecond(subscription.getStartDate()),
                Instant.ofEpochSecond(subscription.getEndedAt())
        );

        subscriptionService.createOrUpdate(user.getStripeCustomer(), subscriptionDTO, stripePlan);
    }

    @Transactional
    public UserSubscription handleSubscriptionCancellation(Subscription subscription) {
        String customerId = subscription.getCustomer();
        User user = userRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        String productId = subscription.getItems().getData().getFirst().getPlan().getProduct();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

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

        return subscriptionService.createOrUpdate(user.getStripeCustomer(), subscriptionDTO, stripePlan);
    }
}
