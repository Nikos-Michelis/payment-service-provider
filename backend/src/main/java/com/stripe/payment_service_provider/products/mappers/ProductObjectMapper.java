package com.stripe.payment_service_provider.products.mappers;

import com.stripe.payment_service_provider.products.dto.ProductDTO;
import com.stripe.payment_service_provider.products.model.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductObjectMapper {

    ProductDTO toDto(Product product);
    List<ProductDTO> toDtoList(List<Product> products);
}
