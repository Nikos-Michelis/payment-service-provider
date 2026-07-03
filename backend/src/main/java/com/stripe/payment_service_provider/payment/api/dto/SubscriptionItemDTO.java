package com.stripe.payment_service_provider.payment.api.dto;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.StripePrice;

public record SubscriptionItemDTO(StripePlan stripePlan, StripePrice stripePrice) {}
