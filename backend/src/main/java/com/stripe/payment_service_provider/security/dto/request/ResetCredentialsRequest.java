package com.stripe.payment_service_provider.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetCredentialsRequest {
    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotNull(message = "Email is mandatory")
    private String email;
}
