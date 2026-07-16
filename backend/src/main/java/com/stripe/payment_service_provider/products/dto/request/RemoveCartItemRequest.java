package com.stripe.payment_service_provider.products.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RemoveCartItemRequest(@NotNull UUID orderLineUUID) {}
