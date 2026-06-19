package com.stripe.payment_service_provider.security.repository;

import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpResendRepository extends JpaRepository<OtpResend, Long> {
    @Query("SELECT o FROM OtpResend o WHERE o.user.id = :userId AND o.otpType = :otpType")
    Optional<OtpResend> findByUserIdAndOtpType(@Param("userId") Long userId, @Param("otpType") String otpType);

}
