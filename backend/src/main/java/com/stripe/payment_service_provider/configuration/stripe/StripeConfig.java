package com.stripe.payment_service_provider.configuration.stripe;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    @Value("${application.api.stripe.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

}
