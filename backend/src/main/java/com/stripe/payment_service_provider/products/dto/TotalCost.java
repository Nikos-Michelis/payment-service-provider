package com.stripe.payment_service_provider.products.dto;

import java.math.BigDecimal;

public record TotalCost(BigDecimal subtotal, BigDecimal total) {}
