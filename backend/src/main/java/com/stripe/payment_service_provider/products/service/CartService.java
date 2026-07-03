package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.CreateCartRequest;
import com.stripe.payment_service_provider.user.model.User;

public interface CartService {
    CartDTO addItemToCart(User user, CreateCartRequest createCartRequest);
    void removeItemFromCart(User user, long itemId);
    void updateOrderLineQuantity(Long orderLineId, int newQuantity);
}
