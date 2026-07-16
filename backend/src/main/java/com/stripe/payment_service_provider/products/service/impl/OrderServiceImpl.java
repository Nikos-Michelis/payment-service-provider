package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.*;
import com.stripe.payment_service_provider.products.repository.OrderRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.service.OrderService;
import com.stripe.payment_service_provider.products.service.PricingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PricingService pricingService;


    @Transactional
    public void createOrder(StripeCustomer stripeCustomer, Set<OrderItem> orderItems) {
        BigDecimal subtotal = BigDecimal.ZERO.setScale(SCALE, ROUNDING);
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            decreaseStock(item.getProduct().getSku(), item.getQuantity());
        }

        for (OrderItem item : orderItems) {
            TotalItemCost itemCost = pricingService.calculateItemCost(item.getProduct(), item.getQuantity());
            subtotal = subtotal.add(itemCost.subtotal());
            total = total.add(itemCost.total());
        }

        BigDecimal shippingCost = BigDecimal.valueOf(2);
        total = total.add(shippingCost);

        Order order = buildBaseOrder(stripeCustomer);
        order.setSubtotal(subtotal);
        order.setTotal(total);
        order.setShipping(shippingCost);
        order.addAllOrderLines(orderItems);

        orderRepository.save(order);
    }


    private Order buildBaseOrder(StripeCustomer stripeCustomer) {
        return Order.builder()
                .customer(stripeCustomer)
                .status(OrderStatus.PENDING)
                .currency("USD")
                .build();
    }


    @Transactional
    public void cancelOrder(UUID orderUUID) {
        Order order = orderRepository.findOrderByUuid(orderUUID)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public void updateOrder() {

    }

    public void decreaseStock(String sku, int quantity) {
        Product product = productRepository.findProductBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Products not found"));

        if (product.getStock() < quantity) {
            throw new IllegalStateException("Product " + product.getSku() + " is out of stock");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

}
