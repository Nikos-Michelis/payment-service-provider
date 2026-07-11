package com.stripe.payment_service_provider.products.dto.request;

public record CartItemRequest(String sku, Integer quantity) {}