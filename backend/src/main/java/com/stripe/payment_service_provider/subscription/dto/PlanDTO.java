package com.stripe.payment_service_provider.subscription.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String planType;
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("billing_cycle")
    private String billingCycle;
    @JsonProperty("token_limit")
    private Integer tokenLimit;
    @JsonProperty("reset_interval")
    private Integer tkResetHoursInterval;
    @JsonProperty("features")
    private List<Object> features;
}
