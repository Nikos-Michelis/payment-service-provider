package com.stripe.payment_service_provider.subscription.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "price")
public class StripePrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "plan_id", nullable = false)
    private StripePlan stripePlan;

    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_price_id", nullable = false)
    private String stripePriceId;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Size(max = 45)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 45)
    private BillingCycle billingCycle;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "active", nullable = false)
    private Boolean active;

}