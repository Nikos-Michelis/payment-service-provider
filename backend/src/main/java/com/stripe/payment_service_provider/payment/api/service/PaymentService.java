package com.stripe.payment_service_provider.payment.api.service;

import com.stripe.exception.StripeException;

public interface PaymentService {
    String createOneOffPayment(String email, String idempotencyKey) throws StripeException;
}
