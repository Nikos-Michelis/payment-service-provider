package com.stripe.payment_service_provider.email.service.impl;

import com.stripe.payment_service_provider.email.EmailTemplateName;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.payment_service_provider.payment.consumer.dto.email.SubscriptionEmailContext;
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

    public void sendSubscriptionSuccessEmail(SubscriptionEmailContext subscriptionEmailContext) {
         try {
             Map<String, Object> properties = new HashMap<>();
             properties.put("appName", appName);
             properties.put("email", subscriptionEmailContext.getEmail());
             properties.put("planName", subscriptionEmailContext.getPlanName());
             properties.put("billingCycle", subscriptionEmailContext);
             properties.put("amount", subscriptionEmailContext.getAmount());
             properties.put("currency", subscriptionEmailContext.getCurrency());
             properties.put("invoiceUrl", subscriptionEmailContext.getInvoicePdf());
             properties.put("renewalDate", formatDate(subscriptionEmailContext.getAccessEndDate()));
             properties.put("redirectUrl", frontendUrl + "/profile");

             emailSenderService.sendEmail(
                     subscriptionEmailContext.getEmail(),
                     "Welcome to "  + appName +" Premium!",
                     EmailTemplateName.CREATE_SUBSCRIPTION,
                     properties
             );

             log.info("Subscription success email sent to: {}", subscriptionEmailContext.getEmail());
         } catch (Exception e) {
             log.error("Failed to send subscription success email to: {}", subscriptionEmailContext.getEmail(), e);
         }
    }

    public void sendSubscriptionUpdateEmail(SubscriptionEmailContext subscriptionEmailContext) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", subscriptionEmailContext.getEmail());
            properties.put("planName", subscriptionEmailContext.getPlanName());
            properties.put("previousPlanName", subscriptionEmailContext.getPrevPlanName());
            properties.put("effectiveDate", formatDate(subscriptionEmailContext.getAccessStartDate()));
            properties.put("billingCycle", subscriptionEmailContext.getBillingCycle());
            properties.put("dashboardUrl", frontendUrl + "/dashboard");

            emailSenderService.sendEmail(
                    subscriptionEmailContext.getEmail(),
                    "Your " + appName + " Subscription has been Upgraded!",
                    EmailTemplateName.UPDATE_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription upgrade email sent to: {}", subscriptionEmailContext.getEmail());
        } catch (Exception e) {
            log.error("Failed to send subscription upgrade email to: {}", subscriptionEmailContext.getEmail(), e);
        }
    }

    public void sendSubscriptionExpirationNotification( SubscriptionEmailContext subscriptionEmailContext) {
        try {
            long daysRemaining = ChronoUnit.DAYS.between(Instant.now(), subscriptionEmailContext.getAccessEndDate());
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", subscriptionEmailContext.getEmail());
            properties.put("planName", subscriptionEmailContext.getPlanName());
            properties.put("daysRemaining", daysRemaining);
            properties.put("accessEndDate", formatDate(subscriptionEmailContext.getAccessEndDate()));
            properties.put("billingCycle", subscriptionEmailContext.getBillingCycle());
            properties.put("renewalUrl", frontendUrl + "/dashboard/billing");

            emailSenderService.sendEmail(
                    subscriptionEmailContext.getEmail(),
                    "Your Subscription Expires in " + daysRemaining + " Days",
                    EmailTemplateName.NOTIFICATION_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription expiration notification sent to: {}", subscriptionEmailContext.getEmail());
        } catch (Exception e) {
            log.error("Failed to send subscription expiration email to: {}", subscriptionEmailContext.getEmail(), e);
        }
    }

    /**
     * Send subscription cancelled email
     */
    public void sendSubscriptionCancelledEmail(SubscriptionEmailContext subscriptionEmailContext) {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", subscriptionEmailContext.getEmail());
            properties.put("planName", subscriptionEmailContext.getPlanName());
            properties.put("cancellationDate", formatDate(Instant.now()));
            properties.put("accessEndDate", formatDate(subscriptionEmailContext.getAccessEndDate()));
            properties.put("dashboardUrl", frontendUrl + "/dashboard");

            emailSenderService.sendEmail(
                    subscriptionEmailContext.getEmail(),
                    "Your Subscription Has Been Cancelled",
                    EmailTemplateName.CANCELLED_SUBSCRIPTION,
                    properties
            );

            log.info("Subscription cancellation email sent to: {}", subscriptionEmailContext.getEmail());
        } catch (Exception e) {
            log.error("Failed to send subscription cancellation email to: {}", subscriptionEmailContext.getEmail(), e);
        }
    }

    public void sendSubscriptionRenewalEmail(SubscriptionEmailContext context) {
        try {
            // Convert the Context to a Map for Thymeleaf
            Map<String, Object> properties = new HashMap<>();
            properties.put("appName", appName);
            properties.put("email", context.getEmail());
            properties.put("planName", context.getPlanName());
            properties.put("renewalDate", context.getAccessStartDate());
            properties.put("nextBillingDate", context.getAccessEndDate());
            properties.put("billingCycle", context.getBillingCycle());
            properties.put("amount", context.getAmount());
            properties.put("invoicePdf", context.getInvoicePdf());
            properties.put("paymentMethod", context.getPaymentMethod());
            properties.put("dashboardUrl", frontendUrl + "/dashboard");

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
