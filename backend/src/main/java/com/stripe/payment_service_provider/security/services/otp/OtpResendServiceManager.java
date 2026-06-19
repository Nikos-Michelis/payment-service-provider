package com.stripe.payment_service_provider.security.services.otp;

import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.user.model.User;

public interface OtpResendServiceManager {
    void saveOtpResend(OtpResend otpResend, OtpToken otpToken, User user);
    void resetOtpResendCount(OtpResend otpResend);
}
