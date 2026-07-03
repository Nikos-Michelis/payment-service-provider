package com.stripe.payment_service_provider.products.controller;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.CreateCartRequest;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.service.CartService;
import com.stripe.payment_service_provider.user.dto.response.ResponseDTO;
import com.stripe.payment_service_provider.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart/add")
    public ResponseEntity<?> addItemToCart(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCartRequest createCartRequest
    ) {
        CartDTO cart = cartService.addItemToCart(user, createCartRequest);
        return ResponseEntity.ok(ResponseDTO.builder()
                .timestamp(Instant.now())
                .message("Item successfully added to the cart.")
                .data(cart)
                .build());
    }

}
