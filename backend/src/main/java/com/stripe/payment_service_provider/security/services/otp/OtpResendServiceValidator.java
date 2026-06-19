package com.stripe.payment_service_provider.security.services.otp;

import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import com.stripe.payment_service_provider.user.model.User;

public interface OtpResendServiceValidator {
    void validateResendAttempts(OtpResend otpResend, User user);
    boolean isExceedMaxAttempts(OtpResend otpResend, User user);
    boolean isUnderCooldown(OtpResend otpResend);
}
