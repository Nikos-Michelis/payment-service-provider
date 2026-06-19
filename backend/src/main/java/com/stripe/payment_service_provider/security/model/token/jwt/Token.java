package com.stripe.payment_service_provider.security.model.token.jwt;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "token", schema = "moonkey_db")
@EqualsAndHashCode(of = "id",  callSuper = false)
public class Token extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    @Column(name = "jti")
    private String jti;
    @Column(name = "token", columnDefinition = "TEXT")
    private String token;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType tokenType = TokenType.BEARER;
    @Enumerated(EnumType.STRING)
    @Column(name = "token_scope")
    private TokenScope tokenScope;
    @Column(name = "revoked")
    public boolean revoked;
    @Column(name = "expired")
    public boolean expired;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}