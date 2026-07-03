package com.stripe.payment_service_provider.payment.api.dto.payment.request;

import jakarta.validation.constraints.NotNull;

public record SubscriptionRequestDTO(
        @NotNull(message = "productId is mandatory")
        String productId,
        @NotNull(message = "priceId is mandatory")
        String priceId
) {}
