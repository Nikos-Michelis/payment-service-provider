package com.stripe.payment_service_provider.subscription.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscription_usage")
@EqualsAndHashCode(of = "id", callSuper = false)
public class SubscriptionUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_usage_id", nullable = false)
    private Long id;

    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_id")
    private UserSubscription userSubscription;

    @Column(name = "expense_id", nullable = false)
    private String expenseId;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;

    @Column(name = "usage_date", nullable = false)
    private Instant usageDate;
}