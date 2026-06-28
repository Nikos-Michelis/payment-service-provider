package com.stripe.payment_service_provider.payment.consumer.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.payment_service_provider.payment.consumer.model.StripePaymentMethod;

public interface StripePaymentMethodService {
    StripePaymentMethod addPaymentMethod(PaymentMethod paymentMethod) throws StripeException;
    StripePaymentMethod removePaymentMethod(PaymentMethod paymentMethod) throws StripeException;
}
