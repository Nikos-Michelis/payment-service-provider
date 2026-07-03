package com.stripe.payment_service_provider.payment.api.dto.email;

import com.stripe.payment_service_provider.subscription.model.BillingCycle;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionEmailContext {
    private String email;
    private String planName;
    private String prevPlanName;
    private BillingCycle billingCycle;
    private String redirectUrl;
    private Long amount;
    private String currency;
    private String invoicePdf;
    private String paymentMethod;

    private Instant accessStartDate;
    private Instant accessEndDate;
    private Instant cancellationDate;
    private String previousPlanName;
    private Integer daysRemaining;
}