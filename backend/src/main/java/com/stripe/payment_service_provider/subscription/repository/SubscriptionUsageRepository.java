package com.stripe.payment_service_provider.subscription.repository;

import com.stripe.payment_service_provider.subscription.dto.SubscriptionUsageDTO;
import com.stripe.payment_service_provider.subscription.model.SubscriptionUsage;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, Long> {

    @Query("""
         SELECT SUM(u.usageCount)
         FROM SubscriptionUsage u
         WHERE u.usageDate >= :startDate AND u.usageDate <= :endDate
         AND u.userSubscription = :userSubscription
    """)
    Long sumUsageBySubscriptionAndDateRange(
            @Param("subscription") UserSubscription userSubscription,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query("""
    SELECT new com.stripe.payment_service_provider.subscription.dto.SubscriptionUsageDTO(u.id, u.usageDate)
        FROM SubscriptionUsage u
        INNER JOIN u.userSubscription s
        GROUP BY u.id, u.usageDate, s.stripePlan.tkResetHoursInterval
        HAVING MAX(u.usageDate) <= CURRENT_TIMESTAMP() - s.stripePlan.tkResetHoursInterval MINUTE
    """)
    List<SubscriptionUsageDTO> findAllExpiredUsages();
}