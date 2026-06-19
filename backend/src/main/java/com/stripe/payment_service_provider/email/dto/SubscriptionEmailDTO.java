package com.stripe.payment_service_provider.email.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class SubscriptionEmailDTO {
    private String appName;
    private String email;
    private String planName;
    private String billingCycle;
    private String dashboardUrl;

    private String amount;
    private String currency;
    private String invoiceUrl;
    private String paymentMethod;

    private String renewalDate;
    private String nextBillingDate;
    private String effectiveDate;
    private String expirationDate;
    private String cancellationDate;
    private String accessEndDate;
    private String previousPlanName;
    private String daysRemaining;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        addIfNotNull(map, "appName", appName);
        addIfNotNull(map, "email", email);
        addIfNotNull(map, "name", planName);
        addIfNotNull(map, "billingCycle", billingCycle);
        addIfNotNull(map, "dashboardUrl", dashboardUrl);
        addIfNotNull(map, "amount", amount);
        addIfNotNull(map, "currency", currency);
        addIfNotNull(map, "invoiceUrl", invoiceUrl);
        addIfNotNull(map, "paymentMethod", paymentMethod);
        addIfNotNull(map, "renewalDate", renewalDate);
        addIfNotNull(map, "nextBillingDate", nextBillingDate);
        addIfNotNull(map, "effectiveDate", effectiveDate);
        addIfNotNull(map, "expirationDate", expirationDate);
        addIfNotNull(map, "cancellationDate", cancellationDate);
        addIfNotNull(map, "accessEndDate", accessEndDate);
        addIfNotNull(map, "previousPlanName", previousPlanName);
        addIfNotNull(map, "daysRemaining", daysRemaining);

        return map;
    }

    private void addIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
