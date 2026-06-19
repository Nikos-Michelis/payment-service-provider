package com.stripe.payment_service_provider.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.model.StripePaymentMethod;

public interface StripePaymentMethodService {
    StripePaymentMethod addPaymentMethod(PaymentMethod paymentMethod) throws StripeException;
}
