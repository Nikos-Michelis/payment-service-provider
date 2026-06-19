package com.stripe.payment_service_provider.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.dto.payment.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.user.model.User;

public interface StripeAccountService {
    SessionResponseDTO getStripeAccountSettings(String email, String idempotencyKey) throws StripeException;
    PaymentMethodDTO getDefaultPaymentMethod(StripeCustomer stripeCustomer) throws StripeException;
}
