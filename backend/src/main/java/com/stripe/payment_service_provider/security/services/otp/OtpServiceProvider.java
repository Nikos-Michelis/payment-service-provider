package com.stripe.payment_service_provider.security.services.otp;

import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.email.EmailDetails;
import com.stripe.payment_service_provider.security.model.otp.OtpType;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.mail.MessagingException;

public interface OtpServiceProvider {
    void buildEmail(User user, OtpToken otpToken, EmailDetails emailDetails) throws MessagingException;
    OtpToken issueOtpCode(User user, OtpType otpType);
}
