package com.stripe.payment_service_provider.payment.consumer.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.products.model.Order;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "stripe_customers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "customerId", callSuper = false)
public class StripeCustomer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Size(max = 255)
    @Column(name = "stripe_customer_id", nullable = false)
    private String stripeCustomerId;
    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "stripeCustomer")
    private Set<UserSubscription> subscriptions;

    @OneToMany(mappedBy = "customer")
    private Set<StripePaymentMethod> stripePaymentMethods;

    @OneToMany(mappedBy = "customer")
    private Set<CustomerPortal> customerPortals = new LinkedHashSet<>();

    @OneToMany(mappedBy = "stripeCustomer")
    private Set<Order> orders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "customer")
    private Set<StripePayment> payments = new LinkedHashSet<>();
}