package com.stripe.payment_service_provider.payment.api.dto;

public record PaymentMethodDTO(String last4, String brand, String type) {
}
