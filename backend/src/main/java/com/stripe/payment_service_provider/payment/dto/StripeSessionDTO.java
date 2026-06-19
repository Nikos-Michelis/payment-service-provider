package com.stripe.payment_service_provider.payment.dto;

public record StripeSessionDTO(String sessionId, String sessionUrl, String type) {}