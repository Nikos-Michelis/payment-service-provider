package com.stripe.payment_service_provider.products.controller;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.request.CartItemRequest;
import com.stripe.payment_service_provider.products.service.CartService;
import com.stripe.payment_service_provider.user.dto.response.ResponseDTO;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart/items")
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal User user) {
        CartDTO cart = cartService.getAllUserCartItems(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/items")
    public ResponseEntity<?> addCartItem(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest cartItemRequest
    ) {
        CartDTO cart = cartService.addItemToCart(user, cartItemRequest);
        return ResponseEntity.ok(ResponseDTO.builder()
                .timestamp(Instant.now())
                .message("Item successfully added to the cart.")
                .data(cart)
                .build());
    }

    @PatchMapping("/cart/items/{uuid}")
    public ResponseEntity<?> updateCartItemQuantity(
            @AuthenticationPrincipal User user,
            @RequestBody CartItemRequest cartItemRequest,
            @Valid
            @NotNull(message = "OrderLine uuid is required.")
            @PathVariable UUID uuid
    ) {
        CartDTO cart = cartService.updateItemQuantity(user, cartItemRequest, uuid);
        return ResponseEntity.ok(ResponseDTO.builder()
                .timestamp(Instant.now())
                .message("Item quantity successfully updated.")
                .data(cart)
                .build());
    }

    @DeleteMapping("/cart/items/{uuid}")
    public ResponseEntity<?> removeCartItem(
            @AuthenticationPrincipal User user,
            @Valid
            @NotNull(message = "OrderLine uuid is required.")
            @PathVariable UUID uuid
    ) {
        CartDTO cart = cartService.removeItemFromCart(user, uuid);
        return ResponseEntity.ok(ResponseDTO.builder()
                .timestamp(Instant.now())
                .message("Item successfully removed from the cart.")
                .data(cart)
                .build());
    }

    @DeleteMapping("/cart/items")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}
