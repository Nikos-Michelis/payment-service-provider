package com.stripe.payment_service_provider.products.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartDTO (
        UUID uuid,
        Integer totalItems,
        BigDecimal subtotal,
        BigDecimal total,
        Set<CartItemDTO> cartItems) {}
