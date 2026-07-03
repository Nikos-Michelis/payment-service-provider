package com.stripe.payment_service_provider.products.dto;

import jakarta.validation.constraints.NotNull;

public record CartItemDTO (
    @NotNull
    String sku,
    @NotNull
    Integer quantity
) {}
