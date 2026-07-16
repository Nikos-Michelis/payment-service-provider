package com.stripe.payment_service_provider.products.repository.specifications;

import com.stripe.payment_service_provider.products.model.Category;
import com.stripe.payment_service_provider.products.model.Product;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> hasSearchKey(String key) {
        String pattern = "%" + key.toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("brand")), pattern),
                cb.like(cb.lower(root.get("sku")), pattern),
                cb.like(cb.lower(root.get("barcode")), pattern)
        );
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, builder) ->
                builder.equal(builder.lower(root.get("brand")), brand.toLowerCase());
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, builder) -> {
            Join<Product, Category> categoryJoin = root.join("rocket", JoinType.LEFT);
            return builder.equal(categoryJoin.get("categoryId"), categoryId);
        };
    }

    public static Specification<Product> hasSku(String sku) {
        return (root, query, builder) ->
                builder.equal(builder.lower(root.get("sku")), sku.toLowerCase());
    }

    public static Specification<Product> hasBarcode(String barcode) {
        return (root, query, builder) ->
                builder.equal(root.get("barcode"), barcode);
    }

    public static Specification<Product> hasAvailabilityStatus(String status) {
        return (root, query, builder) ->
                builder.equal(builder.lower(root.get("availabilityStatus")), status.toLowerCase());
    }

    public static Specification<Product> hasRatingGreaterThanOrEqual(BigDecimal rating) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("rating"), rating);
    }

    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {

        return (root, query, builder) -> {
            if (minPrice != null && maxPrice != null) {
                return builder.between(root.get("price"), minPrice, maxPrice);
            }

            if (minPrice != null) {
                return builder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }

            return builder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }
}
