package com.stripe.payment_service_provider.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressDTO(

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Street is required")
        @Size(max = 255, message = "Street must not exceed 255 characters")
        String street,

        @NotBlank(message = "House number is required")
        @Size(max = 20, message = "House number must not exceed 20 characters")
        String houseNumber,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must not exceed 100 characters")
        String city,

        @Size(max = 100, message = "State must not exceed 100 characters")
        String state,

        @NotBlank(message = "Postal code is required")
        @Size(max = 20, message = "Postal code must not exceed 20 characters")
        String postalCode,

        @NotBlank(message = "Country is required")
        @Pattern(
                regexp = "^[A-Z]{2}$",
                message = "Country must be a valid ISO 3166-1 alpha-2 code"
        )
        String country,

        @Size(max = 50, message = "Floor must not exceed 50 characters")
        String floor,

        @Size(max = 50, message = "Apartment must not exceed 50 characters")
        String apartment,

        @Size(max = 50, message = "Phone must not exceed 50 characters")
        String phone,

        @Size(max = 500, message = "Notes must not exceed 500 characters")
        String notes,

        Double latitude,

        Double longitude
) {}