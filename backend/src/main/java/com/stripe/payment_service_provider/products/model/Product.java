package com.stripe.payment_service_provider.products.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 100)
    @Column(name = "brand", length = 100)
    private String brand;

    @Size(max = 100)
    @Column(name = "sku", length = 100)
    private String sku;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Size(max = 500)
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "active", nullable = false)
    private Boolean active = false;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}