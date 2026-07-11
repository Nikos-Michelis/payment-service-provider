package com.stripe.payment_service_provider.payment.api.controller;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.api.service.PaymentService;
import com.stripe.payment_service_provider.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<?> oneOffPayment(@AuthenticationPrincipal User user, @RequestHeader(value="Idempotency-Key") String idempotencyKey) throws StripeException {
        String response = paymentService.createOneOffPayment(user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
