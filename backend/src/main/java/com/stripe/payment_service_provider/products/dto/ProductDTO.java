package com.stripe.payment_service_provider.products.dto;

import java.math.BigDecimal;

public record ProductDTO(
        String uuid,
        Long externalId,
        CategoryDTO category,
        String title,
        String description,
        String brand,
        String sku,
        BigDecimal price,
        BigDecimal discountPercentage,
        BigDecimal rating,
        Integer stock,
        BigDecimal weight,
        String warrantyInformation,
        String shippingInformation,
        String availabilityStatus,
        String returnPolicy,
        Integer minimumOrderQuantity,
        String barcode,
        String qrCodeUrl,
        String thumbnailUrl
){}
