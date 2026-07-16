package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.CartItem;
import com.stripe.payment_service_provider.products.model.OrderItem;

import java.util.Set;

public interface OrderService {
    void createOrder(StripeCustomer stripeCustomer, Set<OrderItem> orderItems);
    Set<OrderItem> buildOrderItems(Set<CartItem> cartItems);
}
