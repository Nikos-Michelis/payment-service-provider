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
@Table(name = "otp_resend")
@EqualsAndHashCode(of = "id",  callSuper = false)
public class OtpResend extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resend_id")
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @Basic
    @Column(name = "otp_type")
    private String otpType;
    @Basic
    @Column(name = "otp_resend_count")
    private int otpResendCount;
    @Basic
    @Column(name = "last_otp_sent_time")
    private Instant lastOtpSentTime;
}
