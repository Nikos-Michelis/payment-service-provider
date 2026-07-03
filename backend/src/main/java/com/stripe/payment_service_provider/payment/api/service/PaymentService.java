package com.stripe.payment_service_provider.payment.api.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.api.dto.payment.request.OrderLineRequestDTO;

import java.util.List;

public interface PaymentService {
    String createOneTimePayment(List<OrderLineRequestDTO> orderLines, String email, String idempotencyKey) throws StripeException;
}
