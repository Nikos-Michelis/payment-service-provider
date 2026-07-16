package com.stripe.payment_service_provider.payment.api.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.products.dto.request.ShippingMethodRequest;

public interface PaymentService {
    String createOneOffPayment(String email, String idempotencyKey, ShippingMethodRequest shippingMethodRequest) throws StripeException;
}
