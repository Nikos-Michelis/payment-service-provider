package com.stripe.payment_service_provider.security.model.token.reset;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reset_token")
@EqualsAndHashCode(of = "id",  callSuper = false)
public class ResetToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Column(name = "token")
    private String token;
    @Column(name = "expired")
    private boolean expired;
    @Column(name = "revoked")
    private boolean revoked;
    @Column(name = "redeemed")
    private boolean isRedeemed;
    @Column(name = "validated_at")
    private Instant validatedAt;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;
}
