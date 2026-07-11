package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.dto.TotalCostDTO;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.CartItem;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingServiceImpl implements PricingService {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    @Override
    public TotalCostDTO calculateCartCost(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            TotalItemCost itemCost = calculateItemCost(item.getProduct(), item.getQuantity());
            subtotal = subtotal.add(itemCost.subtotal());
            total = total.add(itemCost.total());
        }

        return new TotalCostDTO(subtotal, total);
    }

    @Override
    public TotalCostDTO calculateOrderCost(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            TotalItemCost itemCost = calculateItemCost(item.getProduct(), item.getQuantity());
            subtotal = subtotal.add(itemCost.subtotal());
            total = total.add(itemCost.total());
        }

        return new TotalCostDTO(subtotal, total);
    }
    @Override
    public TotalItemCost calculateItemCost(Product product, Integer quantity) {
        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        BigDecimal discountRate = BigDecimal.valueOf(product.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);

        BigDecimal discount = subtotal.multiply(discountRate);

        BigDecimal taxableAmount = subtotal.subtract(discount);

        BigDecimal taxRate = BigDecimal.valueOf(product.getTax())
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);

        BigDecimal tax = taxableAmount.multiply(taxRate);

        BigDecimal total = taxableAmount.add(tax);
        return new TotalItemCost(subtotal, discount, tax, total);
    }
}
