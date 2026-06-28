package com.stripe.payment_service_provider.payment.workers;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.consumer.dto.SubscriptionDTO;
import com.stripe.payment_service_provider.payment.consumer.service.StripeSubscriptionService;
import com.stripe.payment_service_provider.payment.consumer.util.CustomerUtil;
import com.stripe.payment_service_provider.payment.consumer.util.ProductUtil;
import com.stripe.payment_service_provider.payment.consumer.util.SubscriptionUtil;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import com.stripe.model.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionReconciliationScheduled {
    private final StripeSubscriptionService stripeSubscriptionService;
    private final UserSubscriptionServiceImpl subscriptionService;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionUtil subscriptionUtil;
    private final ProductUtil productUtil;
    private final CustomerUtil customerUtil;
    //@Scheduled(cron = "0 */1 * * * *")
//    @Transactional
//    public void validateUnsubscribedUsers() throws StripeException {
//        List<User> unsubscribedUsers = userRepository.findAllUnsubscribedUsers();
//
//        if (unsubscribedUsers.isEmpty()) {
//            return;
//        }
//
//        Map<String, User> unsubscribedUserMap = getUsersMap(unsubscribedUsers);
//        List<Subscription> subscriptions = subscriptionUtil.getLatestSubscriptionPerCustomer();
//        for (Subscription subscription : subscriptions) {
//            Optional<Customer> customer = customerUtil.findCustomerByCustomerId(subscription.getCustomer());
//
//            if (customer.isEmpty()) {
//                continue;
//            }
//
//            if (unsubscribedUserMap.containsKey(customer.get().getId())) {
//                User user = unsubscribedUserMap.get(customer.get().getEmail());
//                Product product = productUtil.getProductBySubscription(subscription);
//                StripePlan stripePlan = getSubscriptionPlanById(product.getId());
//                SubscriptionDTO subscriptionDTO = getStripeSubscriptionDTO(subscription);
//                subscriptionService.createOrUpdate(user.getStripeCustomer(), subscriptionDTO, stripePlan);
//            }
//        }
//        log.info("Subscription validation for unsubscribed users completed");
//    }
//
//    //@Scheduled(cron = "0 */1 * * * *")
//    @Transactional
//    public void validateSubscribedUsers() throws StripeException {
//        List<User> subscribedUsers = userRepository.findAllSubscribedUsers();
//
//        if (subscribedUsers.isEmpty()) {
//            return;
//        }
//
//        Map<String, User> subscribedUserMap = getUsersMap(subscribedUsers);
//        List<Subscription> subscriptions = subscriptionUtil.getLatestSubscriptionPerCustomer();
//
//        for (Subscription subscription : subscriptions) {
//
//            Customer customer = Customer.retrieve(subscription.getCustomer());
//
//            Product product = productUtil.getProductBySubscription(subscription);
//
//            if (!subscribedUserMap.containsKey(customer.getEmail())) {
//                continue;
//            }
//
//            User user = subscribedUserMap.get(customer.getEmail());
//
//            UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(user.getStripeCustomer().getSubscriptions())
//                    .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));
//
//            String currentStripePlanId = getCurrentPlan(userSubscription).getStripeProductId();
//
//            if (currentStripePlanId.equals(product.getId())) {
//                StripePlan stripePlan = getSubscriptionPlanById(product.getId());
//                SubscriptionDTO subscriptionDTO = getStripeSubscriptionDTO(subscription);
//                subscriptionService.createOrUpdate(user.getStripeCustomer(), subscriptionDTO, stripePlan);
//            }
//        }
//
//
//        log.info("Subscription validation for subscribed users completed");
//    }

    private StripePlan getCurrentPlan(UserSubscription userSubscription) {
        return userSubscription.getStripePlan();
    }

    private Map<String, User> getUsersMap(List<User> users) {
        return users.stream().collect(Collectors.toMap(User::getEmail, user -> user));
    }

    private StripePlan getSubscriptionPlanById(String planId) {
        return planRepository.findSubscriptionPlanByStripeProductId(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + planId));
    }

    private SubscriptionDTO getStripeSubscriptionDTO(Subscription subscription) {
        return new SubscriptionDTO(
                subscription.getId(),
                SubscriptionStatus.valueOf(subscription.getStatus().toUpperCase()),
                Instant.ofEpochSecond(subscription.getStartDate()),
                Instant.ofEpochSecond(subscription.getEndedAt())
        );
    }
}
