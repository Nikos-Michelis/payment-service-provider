package com.stripe.payment_service_provider.subscription.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDTO {
    @JsonProperty("product_id")
    private String stripeProductId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String planType;
    @JsonProperty("prices")
    private Set<PriceDTO> prices;
    @JsonProperty("token_limit")
    private Integer tokenLimit;
    @JsonProperty("reset_interval")
    private Integer tkResetHoursInterval;
    @JsonProperty("features")
    private List<Object> features;
}
