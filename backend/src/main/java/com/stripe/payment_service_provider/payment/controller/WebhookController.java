package com.stripe.payment_service_provider.payment.controller;

import com.stripe.payment_service_provider.payment.service.StripeWebhookHandler;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {
    @Value("${application.api.stripe.webhook.key}")
    private String endpointSecret;
    private final StripeWebhookHandler stripeWebhookHandler;

    @PostMapping("/stripe/event")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            stripeWebhookHandler.handleStripeEvent(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid webhook signature", e);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
