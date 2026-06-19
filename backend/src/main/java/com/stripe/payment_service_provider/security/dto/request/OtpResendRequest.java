package com.stripe.payment_service_provider.security.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OtpResendRequest(
    @NotBlank(message = "Token must not be blank")
    String token){
}
