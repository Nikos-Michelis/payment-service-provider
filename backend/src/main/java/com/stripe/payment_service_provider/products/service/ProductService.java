package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.products.dto.PageSortingDTO;
import com.stripe.payment_service_provider.products.dto.ProductDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ProductService {
    Page<ProductDTO> searchProducts(Map<String, String> requestParams, PageSortingDTO pageSortingDTO);
}
