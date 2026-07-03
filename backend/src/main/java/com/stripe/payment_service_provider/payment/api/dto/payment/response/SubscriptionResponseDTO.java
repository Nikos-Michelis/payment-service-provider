package com.stripe.payment_service_provider.payment.api.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.payment_service_provider.payment.api.dto.PaymentMethodDTO;

import java.math.BigDecimal;

public record SubscriptionResponseDTO(
        @JsonProperty("id")
        String subscriptionId,
        @JsonProperty("plan")
        String plan,
        @JsonProperty("interval")
        String interval,
        @JsonProperty("subscribed_on")
        String subscribedOn,
        @JsonProperty("next_payment_date")
        String nextPaymentDate,
        @JsonProperty("amount")
        BigDecimal amount,
        @JsonProperty("currency")
        String currency,
        @JsonProperty("payment_method")
        PaymentMethodDTO paymentMethodDTO
) {
}
