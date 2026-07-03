package com.stripe.payment_service_provider.products.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CartDTO (UUID uuid, Set<CartItemDTO> orderLines) {}
