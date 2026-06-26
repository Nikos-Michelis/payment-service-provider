package com.stripe.payment_service_provider.subscription.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PriceDTO {
    @JsonProperty("stripe_price_id")
    private String stripePriceId;
    @JsonProperty("amount")
    @Digits(integer = 8, fraction = 0)
    private BigDecimal amount;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("billing_cycle")
    private String billingCycle;
}
