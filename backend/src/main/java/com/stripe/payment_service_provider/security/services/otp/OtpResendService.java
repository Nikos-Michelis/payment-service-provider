package com.stripe.payment_service_provider.security.services.otp;

import com.stripe.payment_service_provider.security.dto.request.OtpResendRequest;
import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.settings.exceptions.auth.OtpLimitException;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.mail.MessagingException;

public interface OtpResendService {
    OtpToken resendOtp(OtpResendRequest otpResendRequest) throws MessagingException, OtpLimitException;
    User isValidUser(User user, OtpResend otpResend);
}
