package com.stripe.payment_service_provider.products.model;

public interface LineItem {
    Product getProduct();
    Integer getQuantity();
}
