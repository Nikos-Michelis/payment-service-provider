package com.stripe.payment_service_provider.products.mappers;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.CartItemDTO;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.CartItem;
import com.stripe.payment_service_provider.products.service.PricingService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CartObjectMapper {

    @Autowired
    protected PricingService pricingService;

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "cartItems", source = "cartItems", qualifiedByName = "sortCartItems")
    public abstract CartDTO toDto(Cart cart);

    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "sku", source = "product.sku")
    @Mapping(target = "title", source = "product.title")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "discount", source = "product.discountPercentage")
    @Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "total",
            expression = "java(pricingService.calculateItemCost(cartItem.getProduct(), cartItem.getQuantity()).total())")
    public abstract CartItemDTO toCartItemDto(CartItem cartItem);

    @Named("sortCartItems")
    protected Set<CartItemDTO> sortCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getSku()))
                .map(this::toCartItemDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}