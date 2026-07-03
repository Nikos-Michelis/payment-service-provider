package com.stripe.payment_service_provider.products.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    private Long id;

    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @Size(max = 3)
    @NotNull
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "subtotal_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalAmount;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @ColumnDefault("0.00")
    @Column(name = "shipping_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingAmount;

    @ColumnDefault("0.00")
    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Size(max = 255)
    @Column(name = "tracking_number")
    private String trackingNumber;

    @Size(max = 255)
    @Column(name = "tracking_url")
    private String trackingUrl;

    @Size(max = 45)
    @Column(name = "carrier", length = 45)
    private String carrier;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private StripeCustomer customer;

    @OneToMany(mappedBy = "order")
    private Set<OrderLine> orderLines = new LinkedHashSet<>();


}