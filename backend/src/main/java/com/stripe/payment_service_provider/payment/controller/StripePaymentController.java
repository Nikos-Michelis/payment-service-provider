package com.stripe.payment_service_provider.payment.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/payment")
public class StripePaymentController {

    /*@PostMapping("/subscription/new")
    public ResponseEntity<?> createSubscription(
            @RequestBody PaymentRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        String response = stripeSubscriptionService.createSubscription(paymentRequest, idempotencyKey);
        return ResponseEntity.ok(response);
    }*/
}
