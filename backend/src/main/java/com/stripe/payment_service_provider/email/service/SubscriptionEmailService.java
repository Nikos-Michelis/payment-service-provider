package com.stripe.payment_service_provider.email.service;

import com.stripe.payment_service_provider.payment.api.dto.email.SubscriptionEmailContext;

public interface SubscriptionEmailService {
    void sendSubscriptionSuccessEmail(SubscriptionEmailContext context);
    void sendSubscriptionUpdateEmail(SubscriptionEmailContext context);
    void sendSubscriptionExpirationNotification(SubscriptionEmailContext context);
    void sendSubscriptionCancelledEmail(SubscriptionEmailContext context);
    void sendSubscriptionRenewalEmail(SubscriptionEmailContext context);
}
