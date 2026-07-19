package com.stripe.payment_service_provider.products.service;

import com.stripe.exception.StripeException;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.products.model.*;

import java.math.BigDecimal;
import java.util.Set;

public interface OrderService {
    Order createOrder(StripeCustomer stripeCustomer, Set<OrderItem> orderItems, BigDecimal shippingCost, Address address) throws StripeException;
    Set<OrderItem> buildOrderItems(Set<CartItem> cartItems);
}
