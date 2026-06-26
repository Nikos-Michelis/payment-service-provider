package com.stripe.payment_service_provider.payment.controller;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.dto.payment.request.OrderLineRequestDTO;
import com.stripe.payment_service_provider.payment.service.PaymentService;
import com.stripe.payment_service_provider.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stripe/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/subscription/new")
    public ResponseEntity<?> createSubscription(
            @AuthenticationPrincipal User user,
            @RequestBody List<OrderLineRequestDTO> orderLineRequestDTO,
            @RequestHeader(value="Idempotency-Key") String idempotencyKey
    ) throws StripeException {
        String response = paymentService.createOneTimePayment(orderLineRequestDTO, user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }
}
