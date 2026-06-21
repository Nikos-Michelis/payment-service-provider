package com.stripe.payment_service_provider.email.service;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.dto.email.SubscriptionEmailContext;
import com.stripe.model.Invoice;

public interface SubscriptionEmailService {
    void sendSubscriptionSuccessEmail(SubscriptionEmailContext context);
    void sendSubscriptionUpdateEmail(SubscriptionEmailContext context);
    void sendSubscriptionExpirationNotification(SubscriptionEmailContext context);
    void sendSubscriptionCancelledEmail(SubscriptionEmailContext context);
    void sendSubscriptionRenewalEmail(SubscriptionEmailContext context);
}
