package com.stripe.payment_service_provider.payment.consumer.dto.payment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.Instant;

@JsonPropertyOrder({ "session_id", "session_url", "created_at" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionResponseDTO(
        @JsonProperty("session_id")
        String sessionId,
        @JsonProperty("session_url")
        String sessionUrl,
        @JsonProperty("session_type")
        String sessionType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
        @JsonProperty("created_at")
        Instant createdAt
) {}
