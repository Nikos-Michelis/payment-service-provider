package com.stripe.payment_service_provider.security.services.otp.impl;

import com.stripe.payment_service_provider.security.dto.request.OtpResendRequest;
import com.stripe.payment_service_provider.security.model.otp.OtpResend;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.security.services.lock.UserStatusService;
import com.stripe.payment_service_provider.security.services.otp.OtpResendService;
import com.stripe.payment_service_provider.security.services.otp.OtpResendServiceManager;
import com.stripe.payment_service_provider.security.services.otp.OtpResendServiceValidator;
import com.stripe.payment_service_provider.security.services.otp.OtpServiceProvider;
import com.stripe.payment_service_provider.email.EmailDetails;
import com.stripe.payment_service_provider.email.EmailTemplateName;
import com.stripe.payment_service_provider.security.repository.OtpRepository;
import com.stripe.payment_service_provider.security.repository.OtpResendRepository;
import com.stripe.payment_service_provider.settings.exceptions.auth.InvalidOtpException;
import com.stripe.payment_service_provider.settings.exceptions.auth.OtpLimitException;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpResendServiceImpl implements OtpResendService {
    private final OtpRepository otpRepository;
    private final OtpResendRepository otpResendRepository;
    private final OtpServiceProvider otpServiceProvider;
    private final UserStatusService userStatusService;
    private final OtpResendServiceValidator otpResendServiceValidator;
    private final OtpResendServiceManager otpResendServiceManager;

    @Override
    public OtpToken resendOtp(OtpResendRequest otpResendRequest) throws MessagingException, OtpLimitException {
        OtpToken otpToken = otpRepository.findByToken(otpResendRequest.token())
                .filter(otp -> (otp.getValidatedAt() == null && !otp.isRedeemed()))
                .orElseThrow(() -> new InvalidOtpException("Invalid otp token."));

        OtpResend otpResend = otpResendRepository
                .findByUserIdAndOtpType(otpToken.getUser().getId(), otpToken.getOtpType().name())
                .orElse(new OtpResend());

        User user = isValidUser(otpToken.getUser(), otpResend);
        if(otpResend.getOtpType() != null) {
            otpResendServiceValidator.validateResendAttempts(otpResend, user);
        }

        otpResendServiceManager.saveOtpResend(otpResend, otpToken, user);
        OtpToken newOtpToken = otpServiceProvider.issueOtpCode(user,  otpToken.getOtpType());
        otpServiceProvider.buildEmail(
                otpToken.getUser(),
                newOtpToken,
                EmailDetails.builder()
                        .subject("Account Verification")
                        .emailTemplateName(EmailTemplateName.VERIFY_ACCOUNT)
                        .build()
        );
        log.warn("newOtpToken: {}", newOtpToken.getOtp());
        return newOtpToken;
    }
    @Override
    public User isValidUser(User user, OtpResend otpResend) {
         if (userStatusService.isAccountLocked(user)) {
             throw new LockedException("Your account is temporarily locked for "
                     + userStatusService.getLockExpiration(user) + ".");
         }
        if(user.isAccountLocked()) {
            if(Instant.now().isAfter(user.getLockExpiresAt())) {
                if (otpResend != null) {
                    otpResendServiceManager.resetOtpResendCount(otpResend);
                }
                if (user.getBlocks() >= 2) {
                    userStatusService.unlockAccount(user);
                }
                userStatusService.resetAccount(user);
            }
        }
         return user;
    }
}
