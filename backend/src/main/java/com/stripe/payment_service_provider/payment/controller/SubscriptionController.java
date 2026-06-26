package com.stripe.payment_service_provider.payment.controller;

import com.stripe.payment_service_provider.payment.dto.payment.response.PaymentResponseDTO;
import com.stripe.payment_service_provider.payment.dto.payment.request.SubscriptionRequestDTO;
import com.stripe.payment_service_provider.payment.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.dto.payment.response.SubscriptionResponseDTO;
import com.stripe.payment_service_provider.payment.service.StripeSubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stripe/payment")
@RequiredArgsConstructor
public class SubscriptionController {
    private final StripeSubscriptionService stripeSubscriptionService;

    @PostMapping("/subscription/new")
    public ResponseEntity<?> createSubscription(
            @AuthenticationPrincipal User user,
            @RequestBody SubscriptionRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        SessionResponseDTO response = stripeSubscriptionService.createSubscription(paymentRequest, user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/upgrade")
    public ResponseEntity<?> upgradeSubscription(
            @AuthenticationPrincipal User user,
            @RequestBody SubscriptionRequestDTO paymentRequest,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        SessionResponseDTO response = stripeSubscriptionService.updateSubscription(paymentRequest, user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/renew")
    public ResponseEntity<?> renewSubscription(@AuthenticationPrincipal User user, @RequestHeader(value="Idempotency-Key") String idempotencyKey) throws StripeException {
        String response = stripeSubscriptionService.renewSubscription(user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscription/list")
    public ResponseEntity<?> getSubscriptions(@AuthenticationPrincipal User user) throws StripeException {
        Optional<List<SubscriptionResponseDTO>> response = stripeSubscriptionService.findSubscriptionByCustomerEmail(user.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subscription/cancel")
    public ResponseEntity<?> cancelSubscription(@AuthenticationPrincipal User user, @RequestHeader(value="Idempotency-Key") String idempotencyKey) throws StripeException {
        SessionResponseDTO response = stripeSubscriptionService.cancelSubscription(user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> sessionSuccess(@PathVariable @NotNull String sessionId) {
        PaymentResponseDTO response = stripeSubscriptionService.checkoutSessionSuccess(sessionId);
        return ResponseEntity.ok(response);
    }
}
