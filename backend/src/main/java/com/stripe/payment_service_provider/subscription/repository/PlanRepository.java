package com.stripe.payment_service_provider.subscription.repository;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<StripePlan, Long> {
    Optional<StripePlan> findSubscriptionPlanByStripeProductId(String id);
}
