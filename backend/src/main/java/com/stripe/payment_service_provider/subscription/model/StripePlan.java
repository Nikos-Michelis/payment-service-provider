package com.stripe.payment_service_provider.subscription.model;
import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan")
@EqualsAndHashCode(of = "id", callSuper = false)
public class StripePlan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id", nullable = false)
    private Long id;
    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_product_id", nullable = false)
    private String stripeProductId;
    @Size(max = 25)
    @NotNull
    @Column(name = "name", nullable = false, length = 25)
    private String name;
    @NotNull
    @Column(name = "description", nullable = false, length = 25)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PlanType planType;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "json", nullable = false)
    private List<Object> features;
    @NotNull
    @ColumnDefault("0")
    @Column(name = "token_limit", nullable = false)
    private Integer tokenLimit;
    @NotNull
    @ColumnDefault("0")
    @Column(name = "tk_reset_hours_interval", nullable = false)
    private Integer tkResetHoursInterval;
    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "stripePlan")
    @BatchSize(size = 20)
    private List<UserSubscription> userSubscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "stripePlan",  cascade = {CascadeType.PERSIST, CascadeType.MERGE,  CascadeType.REFRESH})
    @BatchSize(size = 20)
    private Set<StripePrice> prices = new LinkedHashSet<>();
}