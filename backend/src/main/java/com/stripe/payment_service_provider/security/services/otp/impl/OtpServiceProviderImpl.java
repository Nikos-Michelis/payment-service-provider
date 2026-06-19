package com.stripe.payment_service_provider.security.services.otp.impl;

import com.stripe.payment_service_provider.email.service.AuthenticationEmailService;
import com.stripe.payment_service_provider.security.model.otp.OtpToken;
import com.stripe.payment_service_provider.security.model.otp.OtpType;
import com.stripe.payment_service_provider.security.services.otp.OtpServiceBuilder;
import com.stripe.payment_service_provider.security.services.otp.OtpServiceProvider;
import com.stripe.payment_service_provider.email.EmailDetails;
import com.stripe.payment_service_provider.email.service.impl.EmailSenderServiceImpl;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.security.repository.OtpRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceProviderImpl implements OtpServiceProvider {

    private final OtpRepository otpRepository;
    private final AuthenticationEmailService authenticationEmailService;
    private final OtpServiceBuilder otpServiceBuilder;
    @Override
    public void buildEmail(User user, OtpToken otpToken, EmailDetails emailDetails) throws MessagingException {
        authenticationEmailService.sendOtpEmail(
                user.getEmail(),
                user.getUsername(),
                emailDetails.getEmailTemplateName(),
                emailDetails.getUrl(),
                otpToken.getOtp(),
                emailDetails.getSubject()
        );
    }

    public OtpToken issueOtpCode(User user, OtpType otpType){
        OtpToken otpToken = otpServiceBuilder.generateOtpCode(user, otpType);
        revokeAllUserOtp(user);
        return otpRepository.save(otpToken);
    }

    private void revokeAllUserOtp(User user) {
        var validUserOtp = otpRepository.findAllValidOtpByUser(user.getId());
        if (validUserOtp.isEmpty()) {
            return;
        }
        validUserOtp.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setRedeemed(false);
        });
        otpRepository.saveAll(validUserOtp);
    }
}