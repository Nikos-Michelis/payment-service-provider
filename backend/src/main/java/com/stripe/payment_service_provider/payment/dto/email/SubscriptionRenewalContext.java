package com.stripe.payment_service_provider.payment.dto.email;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class SubscriptionRenewalContext {
    private String appName;
    private String email;
    private String planName;
    private String renewalDate;
    private String nextBillingDate;
    private String billingCycle;
    private String amount;
    private String invoiceUrl;
    private String paymentMethod;
    private String dashboardUrl;
}