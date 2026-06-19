package com.stripe.payment_service_provider.security.services.otp;

import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.security.model.otp.OtpType;
import com.stripe.payment_service_provider.user.model.User;

public interface OtpServiceBuilder {
    OtpToken generateOtpCode(User user, OtpType otpType);
    String generateActivationCode(int length);
}
