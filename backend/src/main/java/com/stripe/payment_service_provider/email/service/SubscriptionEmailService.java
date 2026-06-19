package com.stripe.payment_service_provider.email.service;

import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.dto.email.SubscriptionRenewalContext;
import com.stripe.model.Invoice;

public interface SubscriptionEmailService {
    void sendSubscriptionSuccessEmail(String email, UserSubscription subscription, Invoice invoice);
    void sendSubscriptionUpdateEmail(String email, UserSubscription subscription, String previousPlanName);
    void sendSubscriptionExpirationNotification(String email, UserSubscription subscription);
    void sendSubscriptionCancelledEmail(String email, UserSubscription subscription);
    void sendSubscriptionRenewalEmail(SubscriptionRenewalContext context);
}
