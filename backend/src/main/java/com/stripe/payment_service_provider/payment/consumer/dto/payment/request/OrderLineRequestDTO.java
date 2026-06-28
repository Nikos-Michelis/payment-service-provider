package com.stripe.payment_service_provider.payment.consumer.dto.payment.request;

import jakarta.validation.constraints.NotNull;

public record OrderLineRequestDTO(
        @NotNull(message = "productId is mandatory")
        String productId,
        @NotNull(message = "productId is mandatory")
        Integer quantity
) {}
