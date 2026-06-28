package com.stripe.payment_service_provider.payment.consumer.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.consumer.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.consumer.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.consumer.model.StripeCustomer;

public interface StripeAccountService {
    SessionResponseDTO getStripeAccountSettings(String email, String idempotencyKey) throws StripeException;
    PaymentMethodDTO getDefaultPaymentMethod(StripeCustomer stripeCustomer) throws StripeException;
}
