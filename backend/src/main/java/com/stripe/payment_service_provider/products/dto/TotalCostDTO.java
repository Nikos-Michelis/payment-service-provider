package com.stripe.payment_service_provider.products.dto;

import java.math.BigDecimal;

public record TotalCostDTO(BigDecimal subtotal, BigDecimal total) {}
