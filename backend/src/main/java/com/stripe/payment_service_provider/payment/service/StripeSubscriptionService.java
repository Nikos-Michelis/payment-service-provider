package com.stripe.payment_service_provider.payment.service;


import com.stripe.payment_service_provider.payment.dto.payment.PaymentResponseDTO;
import com.stripe.payment_service_provider.payment.dto.payment.PaymentRequestDTO;
import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.dto.payment.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.dto.payment.SubscriptionResponseDTO;

import java.util.List;
import java.util.Optional;

public interface StripeSubscriptionService {
    SessionResponseDTO createSubscription(PaymentRequestDTO paymentRequest, String email, String idempotencyKey) throws StripeException;
    SessionResponseDTO updateSubscription(PaymentRequestDTO paymentRequest, String email, String idempotencyKey) throws StripeException;
    SessionResponseDTO cancelSubscription(String email, String idempotencyKey) throws StripeException;
    String renewSubscription(String email, String idempotencyKey) throws StripeException;
    Optional<List<SubscriptionResponseDTO>> findSubscriptionByCustomerEmail(String email) throws StripeException;
    PaymentResponseDTO checkoutSessionSuccess(String sessionId);
}
