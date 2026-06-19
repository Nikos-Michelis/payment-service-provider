package com.stripe.payment_service_provider.products.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Size(max = 100)
    @NotNull
    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @OneToMany(mappedBy = "category")
    private Set<Product> products = new LinkedHashSet<>();

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @NotNull
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}