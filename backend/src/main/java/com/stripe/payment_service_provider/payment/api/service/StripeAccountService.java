package com.stripe.payment_service_provider.payment.api.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.api.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.api.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;

public interface StripeAccountService {
    SessionResponseDTO getStripeAccountSettings(String email, String idempotencyKey) throws StripeException;
    PaymentMethodDTO getDefaultPaymentMethod(StripeCustomer stripeCustomer) throws StripeException;
}
