package com.stripe.payment_service_provider.email.service.impl;

import com.stripe.payment_service_provider.email.EmailTemplateName;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.dto.email.SubscriptionRenewalContext;
import com.stripe.model.Invoice;
import com.stripe.payment_service_provider.utils.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionEmailServiceImpl implements SubscriptionEmailService {
    @Value("${application.seo.name}")
    private String appName;
    @Value("${application.frontend.url}")
    private String frontendUrl;
    private static final String CONTACT_URL = "https://www.moonkeyeu.com/contact";
    private final EmailSenderServiceImpl emailSenderService;

    public void sendSubscriptionSuccessEmail(String email, UserSubscription subscription, Invoice invoice) {
         try {
             Map<String, Object> properties = new HashMap<>();
             properties.put("appName", appName);
             properties.put("email", email);
             properties.put("planName", subscription.getStripePlan().getName());
             properties.put("billingCycle", subscription.getStripePlan().getBillingCycle());
             properties.put("amount", invoice.getAmountPaid());
             properties.put("currency", invoice.getCurrency());
             properties.put("invoiceUrl", invoice.getInvoicePdf());
             properties.put("renewalDate", formatDate(subscription.getCurrentPeriodEnd()));
             properties.put("dashboardUrl", frontendUrl + "/dashboard");

             emailSenderService.sendEmail(
                     email,
                     "Welcome to "  + appName +" Premium!",
                     EmailTemplateName.CREATE_SUBSCRIPTION,
                     properties
             );

             log.info("Subscription success email sent to: {}", email);
         } catch (Exception e) {
             log.error("Failed to send subscription success email to: {}", email, e);
         }
    }

    public void sendSubscriptionUpdateEmail(String email, UserSubscription subscription, String previousPlanName) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", email);
            properties.put("planName", subscription.getStripePlan().getName());
            properties.put("previousPlanName", previousPlanName);
            properties.put("effectiveDate", formatDate(Instant.now()));
            properties.put("billingCycle", subscription.getStripePlan().getBillingCycle());
            properties.put("dashboardUrl", frontendUrl + "/dashboard");

            emailSenderService.sendEmail(
                    email,
                    "Your " + appName + " Subscription has been Upgraded!",
                    EmailTemplateName.UPDATE_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription upgrade email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send subscription upgrade email to: {}", email, e);
        }
    }

    public void sendSubscriptionExpirationNotification(String email, UserSubscription subscription) {
        try {
            long daysRemaining = ChronoUnit.DAYS.between(Instant.now(), subscription.getCurrentPeriodEnd());
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", email);
            properties.put("planName", subscription.getStripePlan().getName());
            properties.put("daysRemaining", daysRemaining);
            properties.put("expirationDate", formatDate(subscription.getCurrentPeriodEnd()));
            properties.put("billingCycle", subscription.getStripePlan().getBillingCycle());
            properties.put("renewalUrl", frontendUrl + "/dashboard/billing");

            emailSenderService.sendEmail(
                    email,
                    "Your Subscription Expires in " + daysRemaining + " Days",
                    EmailTemplateName.NOTIFICATION_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription expiration notification sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send subscription expiration email to: {}", email, e);
        }
    }

    /**
     * Send subscription cancelled email
     */
    public void sendSubscriptionCancelledEmail(String email, UserSubscription subscription) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", email);
            properties.put("planName", subscription.getStripePlan().getName());
            properties.put("cancellationDate", formatDate(Instant.now()));
            properties.put("accessEndDate", formatDate(subscription.getCurrentPeriodEnd()));
            properties.put("dashboardUrl", frontendUrl + "/dashboard");

            emailSenderService.sendEmail(
                    email,
                    "Your Subscription Has Been Cancelled",
                    EmailTemplateName.CANCELLED_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription cancellation email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send subscription cancellation email to: {}", email, e);
        }
    }

    public void sendSubscriptionRenewalEmail(SubscriptionRenewalContext context) {
        try {
            // Convert the Context to a Map for Thymeleaf
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", context.getAppName());
            properties.put("email", context.getEmail());
            properties.put("planName", context.getPlanName());
            properties.put("renewalDate", context.getRenewalDate());
            properties.put("nextBillingDate", context.getNextBillingDate());
            properties.put("billingCycle", context.getBillingCycle());
            properties.put("amount", context.getAmount());
            properties.put("invoiceUrl", context.getInvoiceUrl());
            properties.put("paymentMethod", context.getPaymentMethod());
            properties.put("dashboardUrl", context.getDashboardUrl());

            emailSenderService.sendEmail(
                    context.getEmail(),
                    "Your Subscription Has Been Renewed",
                    EmailTemplateName.RENEWAL_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription renewal confirmation sent to: {}", context.getEmail());
        } catch (Exception e) {
            log.error("Failed to send subscription renewal email to: {}", context.getEmail(), e);
        }
    }

    private String formatDate(Instant instant) {
        return DateTimeUtil
                .getDateTimeFormatter("MMMM dd, yyyy", TimeZone.getDefault().toZoneId())
                .format(instant);
    }
}
