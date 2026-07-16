package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.products.dto.TotalCost;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.LineItem;
import com.stripe.payment_service_provider.products.model.Product;

import java.util.Collection;

public interface PricingService {
    TotalCost calculateTotal(Collection<? extends LineItem> lineItems);
    TotalItemCost calculateItemCost(Product product, Integer quantity);
}
