package com.stripe.payment_service_provider.subscription.dto;

import java.time.Instant;

public record SubscriptionUsageDTO (Long subscription_usage_id, Instant last_usage) {}
