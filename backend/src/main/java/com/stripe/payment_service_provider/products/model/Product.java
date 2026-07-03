package com.stripe.payment_service_provider.products.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, length = 36)
    private String uuid;

    @NotNull
    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 100)
    @Column(name = "brand", length = 100)
    private String brand;

    @Size(max = 100)
    @NotNull
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "discount_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    @Size(max = 255)
    @Column(name = "warranty_information")
    private String warrantyInformation;

    @Size(max = 255)
    @Column(name = "shipping_information")
    private String shippingInformation;

    @Size(max = 50)
    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;

    @Size(max = 255)
    @Column(name = "return_policy")
    private String returnPolicy;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "minimum_order_quantity", nullable = false)
    private Integer minimumOrderQuantity;

    @Size(max = 100)
    @Column(name = "barcode", length = 100)
    private String barcode;

    @Size(max = 500)
    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    @Size(max = 500)
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @NotNull
    @Version
    @Column(name = "version")
    private Integer version;

    @OneToMany(mappedBy = "product")
    private Set<OrderLine> orderLines = new LinkedHashSet<>();

    @OneToOne(mappedBy = "product")
    private ProductDimension productDimension;

    @OneToMany(mappedBy = "product")
    private Set<ProductImage> productImages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product")
    private Set<ProductReview> productReviews = new LinkedHashSet<>();

}