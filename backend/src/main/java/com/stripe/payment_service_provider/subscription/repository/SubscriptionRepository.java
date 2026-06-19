package com.stripe.payment_service_provider.subscription.repository;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<UserSubscription, String> {
    Optional<UserSubscription> findUserSubscriptionsByStripeCustomer_StripeCustomerId(String StripeCustomerId);
    Optional<UserSubscription> findUserSubscriptionsByStripeSubscriptionId(String stripeSubscriptionId);
}