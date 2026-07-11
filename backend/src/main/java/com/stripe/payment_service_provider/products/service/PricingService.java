package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.products.dto.TotalCostDTO;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.Product;

public interface PricingService {
    TotalCostDTO calculateCartCost(Cart cart);
    TotalCostDTO calculateOrderCost(Cart cart);
    TotalItemCost calculateItemCost(Product product, Integer quantity);
}
