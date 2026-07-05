package com.stripe.payment_service_provider.products.mappers;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.CartItemDTO;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.OrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface CartObjectMapper {

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "orderLines", source = "orderLines")
    CartDTO toDto(Cart cart);

    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "quantity", source = "quantity")
    CartItemDTO toCartItemDto(OrderLine orderLine);
}