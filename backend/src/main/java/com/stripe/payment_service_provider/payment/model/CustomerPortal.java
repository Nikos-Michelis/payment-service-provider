package com.stripe.payment_service_provider.payment.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customer_portal")
public class CustomerPortal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portal_id", nullable = false)
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private StripeCustomer customer;

    @Size(max = 255)
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;
    @Size(max = 255)
    @Column(name = "session_id")
    private String sessionId;
    @Lob
    @Column(name = "session_url", columnDefinition = "TEXT")
    private String sessionUrl;

    @Size(max = 255)
    @NotNull
    @Column(name = "session_type", nullable = false)
    private String sessionType;

}