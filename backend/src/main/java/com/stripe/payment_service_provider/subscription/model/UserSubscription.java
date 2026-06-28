package com.stripe.payment_service_provider.subscription.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.payment.consumer.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.consumer.model.StripeInvoice;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_subscription")
@EqualsAndHashCode(of = "id", callSuper = false)
public class UserSubscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id", nullable = false)
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;
    @Column(name = "stripe_subscription_id", nullable = false)
    private String stripeSubscriptionId;
    @Column(name = "expiration_reminder_sent")
    private boolean expirationReminderSent;
    @Column(name = "current_period_start")
    private Instant currentPeriodStart;
    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private StripePlan stripePlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_id", nullable = false)
    private StripePrice stripePrice;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH } )
    @JoinColumn(name = "customer_id", nullable = false)
    private StripeCustomer stripeCustomer;
    @OneToMany(mappedBy = "subscription")
    private Set<StripeInvoice> stripeInvoices = new HashSet<>();

    @OneToMany(mappedBy = "userSubscription")
    private Set<SubscriptionUsage> subscriptionUsages = new LinkedHashSet<>();

}
