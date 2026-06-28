package com.stripe.payment_service_provider.payment.consumer.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_method")
public class StripePaymentMethod extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id", nullable = false)
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private StripeCustomer customer;

    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_payment_method_id", nullable = false)
    private String stripePaymentMethodId;

    @Size(max = 255)
    @NotNull
    @Column(name = "fingerprint", nullable = false)
    private String fingerprint;

    @Size(max = 4)
    @NotNull
    @Column(name = "last4", nullable = false, length = 4)
    private String last4;

    @Size(max = 45)
    @NotNull
    @Column(name = "brand", nullable = false, length = 45)
    private String brand;

    @Size(max = 50)
    @NotNull
    @Column(name = "funding", nullable = false, length = 50)
    private String funding;

    @NotNull
    @Column(name = "exp_month", nullable = false)
    private Integer expMonth;

    @NotNull
    @Column(name = "exo_year", nullable = false)
    private Integer exoYear;

    @Size(max = 45)
    @NotNull
    @Column(name = "type", nullable = false, length = 45)
    private String type;

    @ColumnDefault("false")
    @Column(name = "is_default")
    private Boolean isDefault;

    @OneToMany(mappedBy = "paymentMethod")
    private Set<StripePayment> payments = new LinkedHashSet<>();

}