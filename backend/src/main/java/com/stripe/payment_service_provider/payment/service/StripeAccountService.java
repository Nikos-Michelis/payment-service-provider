package com.stripe.payment_service_provider.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;

public interface StripeAccountService {
    SessionResponseDTO getStripeAccountSettings(String email, String idempotencyKey) throws StripeException;
}
