package com.stripe.payment_service_provider.user.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.security.model.EntryMethods;
import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.security.model.token.jwt.Token;
import com.stripe.payment_service_provider.security.model.token.reset.ResetToken;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
@EqualsAndHashCode(of = "id", callSuper = false)
public class User extends BaseEntity implements UserDetails, Principal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Column(name = "username", unique = true)
    private String username;
    @Column(name="email", unique = true)
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "enable")
    private boolean enable;
    @Column(name = "account_locked")
    private boolean accountLocked;
    @Column(name = "blocks")
    private Integer blocks;
    @Column(name = "attempts")
    private Integer attempts;
    @Column(name = "locked_at")
    private Instant lockedAt;
    @Column(name = "lock_expires_at")
    private Instant lockExpiresAt;
    @Column(name = "validated_at")
    private Instant validatedAt;
    @BatchSize(size = 20)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    @BatchSize(size = 20)
    private List<Token> tokens;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    @BatchSize(size = 20)
    private List<OtpToken> otpTokens;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    @BatchSize(size = 20)
    private List<OtpResend> otpResends;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    @BatchSize(size = 20)
    private List<ResetToken> resetTokens;
    @BatchSize(size = 50)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_entry_method",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "method_id")
    )
    private Set<EntryMethods> entryMethods = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private StripeCustomer stripeCustomer;

    public void addSignUpProvider(EntryMethods signUpMethod) {
        this.entryMethods.add(signUpMethod);
    }
    public void removeAllRoles() {
        this.roles.forEach(role -> role.getUsers().remove(this));
        this.roles.clear();
    }

    public void removeAllSignUpMethods() {
        this.entryMethods.forEach(method -> method.getUsers().remove(this));
        this.entryMethods.clear();
    }

    @Override
    public String getName() {
        return email;
    }

   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
       return roles.stream()
               .flatMap(role -> {
                   Set<SimpleGrantedAuthority> authorities = role.getPermissions().stream()
                           .map(permission -> new SimpleGrantedAuthority(permission.getName().getPermission()))
                           .collect(Collectors.toSet());

                   authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                   return authorities.stream();
               })
               .collect(Collectors.toSet());
   }

   @Override
   public String getPassword() {
        return password;
    }
   public String getNickname() { return username; }
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }
}
