package com.stripe.payment_service_provider.security.services.otp;

import com.stripe.payment_service_provider.security.dto.request.OtpValidationRequest;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.user.model.User;

public interface OtpServiceValidator {
    void isValidOtp(OtpToken otpToken, OtpValidationRequest otpValidationRequest, User user);
    void handleInvalidOtp(User user);
}
