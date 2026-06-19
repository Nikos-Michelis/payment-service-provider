package com.stripe.payment_service_provider.security.model.otp;

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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otp")
@EqualsAndHashCode(of = "id",  callSuper = false)
public class OtpToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Column(name = "otp_type")
    @Enumerated(EnumType.STRING)
    private OtpType otpType;
    @Column(name = "token")
    private String token;
    @Column(name = "otp_code")
    private String otp;
    @Column(name = "expired")
    private boolean expired;
    @Column(name = "revoked")
    private boolean revoked;
    @Column(name = "redeemed")
    private boolean isRedeemed;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "validated_at")
    private Instant validatedAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
}
