package com.stripe.payment_service_provider.security.services.otp.impl;

import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.security.services.otp.OtpResendServiceManager;
import com.stripe.payment_service_provider.security.repository.OtpResendRepository;
import com.stripe.payment_service_provider.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpResendServiceManagerImpl implements OtpResendServiceManager {

    private final OtpResendRepository otpResendRepository;

    public void saveOtpResend(OtpResend otpResend, OtpToken otpToken, User user) {
        otpResend.setUser(user);
        otpResend.setOtpType(otpToken.getOtpType().name());
        otpResend.setOtpResendCount(otpResend.getOtpResendCount() + 1);
        otpResend.setLastOtpSentTime(Instant.now());
        log.info("Recorded OTP resend for user {}", otpResend.getUser().getId());
        otpResendRepository.save(otpResend);
    }
    public void resetOtpResendCount(OtpResend otpResend) {
        otpResend.setOtpResendCount(0);
        log.info("OtpResend --> {}", otpResend);
        otpResendRepository.save(otpResend);
        log.info("Reset OTP resend count for user {}", otpResend.getUser().getId());
    }

}
