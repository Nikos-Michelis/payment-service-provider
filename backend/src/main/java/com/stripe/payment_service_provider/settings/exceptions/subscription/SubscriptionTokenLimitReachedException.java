package com.stripe.payment_service_provider.settings.exceptions.subscription;


import org.springframework.http.HttpStatus;

public class SubscriptionTokenLimitReachedException extends SubscriptionException {

    public SubscriptionTokenLimitReachedException() {
        super("Token Limit Reached", "You have reached your token limit. Please try again next interval.", HttpStatus.TOO_MANY_REQUESTS);
    }
}