package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.dto.TotalCost;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.LineItem;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

@Service
public class PricingServiceImpl implements PricingService {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    @Override
    public TotalCost calculateTotal(Collection<? extends LineItem> lineItems) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;

        for (LineItem item : lineItems) {
            TotalItemCost itemCost = calculateItemCost(item.getProduct(), item.getQuantity());
            subtotal = subtotal.add(itemCost.subtotal());
            total = total.add(itemCost.total());
        }

        return new TotalCost(subtotal, total);
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
