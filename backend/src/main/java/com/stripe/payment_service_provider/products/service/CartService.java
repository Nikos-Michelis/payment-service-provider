package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.request.CartItemRequest;
import com.stripe.payment_service_provider.user.model.User;

import java.util.UUID;

public interface CartService {
    CartDTO getAllUserCartItems(User user);
    CartDTO addItemToCart(User user, CartItemRequest cartItemRequest);
    CartDTO updateItemQuantity(User user, CartItemRequest cartItemRequest, UUID orderLineUUID);
    CartDTO removeItemFromCart(User user, UUID OrderLineUUID);
    void clearCart(User user);
}
