package com.stripe.payment_service_provider.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SignUpProvider {
    Google("google"),
    Github("github"),
    Facebook("facebook"),

    Password("password");

    @Getter
    private final String providers;
}
