package com.stripe.payment_service_provider.products.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ShippingMethodRequest(
        @NotNull
        String shipperUUID,
        @NotNull
        String countryCode
) {
}
