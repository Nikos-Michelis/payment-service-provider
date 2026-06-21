package com.stripe.payment_service_provider.payment.controller;

import com.stripe.payment_service_provider.payment.dto.InvoiceDTO;
import com.stripe.payment_service_provider.payment.dto.payment.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.service.StripeAccountService;
import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceService;
import com.stripe.payment_service_provider.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/stripe/account")
@RequiredArgsConstructor
public class AccountController {

    private final StripeAccountService stripeAccountService;
    private final StripeInvoiceService stripeInvoiceService;

    @PostMapping("/billing/setting")
    public ResponseEntity<?> getAccountSettings(@AuthenticationPrincipal User user, @RequestHeader(value="Idempotency-Key") String idempotencyKey) throws StripeException {
        SessionResponseDTO response = stripeAccountService.getStripeAccountSettings(user.getEmail(), idempotencyKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/billing/invoices")
    public ResponseEntity<?> getAccountInvoices(@AuthenticationPrincipal User user) throws StripeException {

        if (user.getStripeCustomer() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<InvoiceDTO> response = stripeInvoiceService.getAllByCustomerId(user.getStripeCustomer().getStripeCustomerId());
        return ResponseEntity.ok(response);
    }
}
