package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.products.dto.TotalCost;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.*;
import com.stripe.payment_service_provider.products.repository.OrderRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.service.OrderService;
import com.stripe.payment_service_provider.products.service.PricingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final PricingService pricingService;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(StripeCustomer stripeCustomer, Set<OrderItem> orderItems, BigDecimal shippingCost, Address address) {
        TotalCost totalCost = pricingService.calculateTotal(orderItems);

        Order order = buildOrder(stripeCustomer);
        order.setSubtotal(totalCost.subtotal());
        order.setTotal(totalCost.total());
        order.setShipping(shippingCost);
        order.addAllOrderLines(orderItems);
        order.setAddress(address);

        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(UUID orderUUID) {
        Order order = orderRepository.findOrderByUuid(orderUUID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());
        orderRepository.save(order);
    }

    public void updateOrder() {

    }

    @Override
    public Set<OrderItem> buildOrderItems(Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findProductBySku(cartItem.getProduct().getSku())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            TotalItemCost itemCost = pricingService.calculateItemCost(product, cartItem.getQuantity());
            OrderItem orderItem = buildOrderItem(product, cartItem.getQuantity(), itemCost.total());
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private Order buildOrder(StripeCustomer stripeCustomer) {
        return Order.builder()
                .customer(stripeCustomer)
                .status(OrderStatus.PENDING)
                .currency("EUR")
                .build();
    }

    private OrderItem buildOrderItem(Product product, int quantity, BigDecimal totalAmount) {
        return OrderItem.builder()
                .product(product)
                .amount(product.getPrice())
                .discount(product.getDiscountPercentage())
                .tax(product.getTax())
                .quantity(quantity)
                .total(totalAmount)
                .build();
    }
}
