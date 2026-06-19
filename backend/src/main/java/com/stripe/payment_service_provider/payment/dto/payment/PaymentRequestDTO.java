package com.stripe.payment_service_provider.payment.dto.payment;

import com.stripe.model.Product;

public record PaymentRequestDTO(String IdempotencyKey, Long amount, String currency, String productId, String priceId) { }
