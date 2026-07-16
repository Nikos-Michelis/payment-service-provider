package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.dto.PageSortingDTO;
import com.stripe.payment_service_provider.products.dto.ProductDTO;
import com.stripe.payment_service_provider.products.mappers.ProductObjectMapper;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.repository.specifications.ProductSpecification;
import com.stripe.payment_service_provider.products.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductObjectMapper productObjectMapper;

    private final Map<String, Function<String, Specification<Product>>> filters = Map.of(
            "brand", ProductSpecification::hasBrand,
            "sku", ProductSpecification::hasSku,
            "barcode", ProductSpecification::hasBarcode,
            "availabilityStatus", ProductSpecification::hasAvailabilityStatus,
            "search", ProductSpecification::hasSearchKey
    );


    public Page<ProductDTO> searchProducts(Map<String, String> requestParams, PageSortingDTO pageSortingDTO) {
        Specification<Product> spec = Specification.unrestricted();

        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            Function<String, Specification<Product>> filter = filters.get(entry.getKey());
            if (filter != null) {
                spec = spec.and(filter.apply(entry.getValue()));
            }
        }

        Sort sortObject = "desc".equalsIgnoreCase(pageSortingDTO.getSort())
                ? Sort.by(pageSortingDTO.getField()).descending()
                : Sort.by(pageSortingDTO.getField()).ascending();

        int page = (pageSortingDTO.getPage() > 0) ? pageSortingDTO.getPage() - 1 : 0;
        Pageable pageable = PageRequest.of(page, pageSortingDTO.getLimit(), sortObject);

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productObjectMapper::toDto);
    }

    @Transactional
    public void decreaseStock(String sku, int quantity) {
        Product product = productRepository.findProductBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Products not found"));

        if (product.getStock() < quantity) {
            throw new IllegalStateException("Product " + product.getSku() + " is out of stock");
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}