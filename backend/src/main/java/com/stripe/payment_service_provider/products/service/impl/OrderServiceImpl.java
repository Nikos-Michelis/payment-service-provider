package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.products.model.*;
import com.stripe.payment_service_provider.products.repository.OrderLineRepository;
import com.stripe.payment_service_provider.products.repository.OrderRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Transactional
    public void createOrder(StripeCustomer stripeCustomer, Cart cart) {
        Set<OrderLine> orderLines = cart.getOrderLines();

        BigDecimal subtotal = BigDecimal.ZERO.setScale(SCALE, ROUNDING);
        BigDecimal totalDiscount = BigDecimal.ZERO.setScale(SCALE, ROUNDING);

        for (OrderLine orderLine : orderLines) {
            Product product = decreaseStock(orderLine.getProduct().getSku(), orderLine.getQuantity());

            BigDecimal unitPrice = product.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(orderLine.getQuantity());
            BigDecimal lineSubtotal = unitPrice.multiply(quantity).setScale(SCALE, ROUNDING);

            BigDecimal lineDiscount = getOrderLineDiscount(product.getDiscountPercentage(), lineSubtotal);

            subtotal = subtotal.add(orderLine.getTotal());
            totalDiscount = totalDiscount.add(lineDiscount);
        }

        BigDecimal taxableAmount = subtotal.subtract(totalDiscount);
        BigDecimal taxAmount = taxableAmount
                .multiply(BigDecimal.valueOf(22))
                .setScale(SCALE, ROUNDING);

        BigDecimal totalAmount = taxableAmount.add(taxAmount);

        Order order = buildBaseOrder(stripeCustomer, orderLines);
        order.setSubtotalAmount(subtotal);
        order.setDiscountAmount(totalDiscount);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    private Order buildBaseOrder(StripeCustomer stripeCustomer, Set<OrderLine> orderLines) {
        return Order.builder()
                .customer(stripeCustomer)
                .status(OrderStatus.PENDING)
                .orderLines(orderLines)
                .currency("USD")
                .build();
    }

    private BigDecimal getOrderLineDiscount(BigDecimal discountPercentage, BigDecimal lineSubtotal) {
        BigDecimal discountPct = discountPercentage != null
                ? discountPercentage
                : BigDecimal.ZERO;

        return lineSubtotal
                .multiply(discountPct)
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);
    }


    public void cancelOrder() {

    }

    public void updateOrder() {

    }

    public Product decreaseStock(String sku, int qty) {
        Product product = productRepository.findProductBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Products not found"));

        if (product.getStock() < qty) {
            throw new IllegalStateException("Out of stock");
        }

        product.setStock(product.getStock() - qty);
        return product;
    }

}
