package com.stripe.payment_service_provider.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@JsonPropertyOrder({"uuid", "title", "sku", "quantity", "price", "discount", "total", "thumbnail_url"})
public class CartItemDTO {
    private UUID uuid;
    private String title;
    private BigDecimal price;
    private Integer discount;
    private BigDecimal total;
    private String sku;
    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
    private Integer quantity;
}