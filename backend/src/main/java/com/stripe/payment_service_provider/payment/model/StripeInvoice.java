package com.stripe.payment_service_provider.payment.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice")
@EqualsAndHashCode(of = "id", callSuper = false)
public class StripeInvoice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id", nullable = false)
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Size(max = 255)
    @NotNull
    @Column(name = "invoice_stripe_id", nullable = false)
    private String invoiceStripeId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "subscription_id", nullable = false)
    private UserSubscription subscription;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "currency", length = 3)
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status = InvoiceStatus.UNCOLLECTIBLE;
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_reason", length = 30)
    private BillingReason billingReason;

    @Size(max = 255)
    @NotNull
    @Column(name = "hosted_invoice_url", nullable = false)
    private String hostedInvoiceUrl;
    @Column(name = "next_payment_attempt")
    private Instant nextPaymentAttempt;
    @Column(name = "invoice_created_at")
    private Instant invoiceCreatedAt;
    @Column(name = "finalized_at")
    private Instant finalizedAt;

    @OneToMany(mappedBy = "invoice")
    private Set<StripePayment> payments = new LinkedHashSet<>();

}