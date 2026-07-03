package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl {

    private final ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public void decreaseStock(Long productId, int qty) {
        Product product = getById(productId);

        if (product.getStock() < qty) {
            throw new IllegalStateException("Out of stock");
        }

        product.setStock(product.getStock() - qty);
    }

    @Transactional
    public Product update(Long id, Product updated) {
        Product product = getById(id);

        product.setTitle(updated.getTitle());
        product.setDescription(updated.getDescription());
        product.setBrand(updated.getBrand());
        product.setSku(updated.getSku());
        product.setPrice(updated.getPrice());
        product.setDiscountPercentage(updated.getDiscountPercentage());
        product.setRating(updated.getRating());
        product.setStock(updated.getStock());
        product.setWeight(updated.getWeight());
        product.setWarrantyInformation(updated.getWarrantyInformation());
        product.setShippingInformation(updated.getShippingInformation());
        product.setAvailabilityStatus(updated.getAvailabilityStatus());
        product.setReturnPolicy(updated.getReturnPolicy());
        product.setMinimumOrderQuantity(updated.getMinimumOrderQuantity());
        product.setBarcode(updated.getBarcode());
        product.setQrCodeUrl(updated.getQrCodeUrl());
        product.setThumbnailUrl(updated.getThumbnailUrl());

        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }
}