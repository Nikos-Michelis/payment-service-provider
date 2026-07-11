package com.stripe.payment_service_provider.products.dto;

import java.math.BigDecimal;

public record TotalItemCost (
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total
){}
