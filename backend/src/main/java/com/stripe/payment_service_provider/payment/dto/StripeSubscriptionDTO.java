package com.stripe.payment_service_provider.payment.dto;

import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StripeSubscriptionDTO {
    private String stripeSubscriptionId;
    private SubscriptionStatus status;
    private boolean expirationReminderSent;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
}
