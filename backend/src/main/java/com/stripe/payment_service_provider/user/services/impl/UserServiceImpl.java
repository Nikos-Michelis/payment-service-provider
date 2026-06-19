package com.stripe.payment_service_provider.user.services.impl;

import com.stripe.payment_service_provider.email.service.AuthenticationEmailService;
import com.stripe.payment_service_provider.security.dto.request.ChangePasswordRequest;
import com.stripe.payment_service_provider.security.dto.request.ResetCredentialsRequest;
import com.stripe.payment_service_provider.security.dto.request.ResetPasswordRequest;
import com.stripe.payment_service_provider.email.EmailTemplateName;
import com.stripe.payment_service_provider.security.model.token.reset.ResetToken;
import com.stripe.payment_service_provider.user.dto.UserDTO;
import com.stripe.payment_service_provider.user.model.Roles;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.security.repository.ResetTokenRepository;
import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import com.stripe.payment_service_provider.security.util.RequestTokenBuilder;
import com.stripe.payment_service_provider.user.services.UserService;
import com.stripe.payment_service_provider.settings.exceptions.auth.InvalidResetTokenException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${application.frontend.url}")
    private String frontendUrl;
    private final UserRepository userRepository;
    private final RequestTokenBuilder requestTokenBuilder;
    private final ResetTokenRepository resetTokenRepository;
    private final AuthenticationEmailService authenticationEmailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void generatePasswordResetToken(ResetCredentialsRequest resetCredentialsRequest) throws MessagingException {
        User user = userRepository.findByEmail(resetCredentialsRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("We cannot find any user with the provided email address."));
        String token = requestTokenBuilder.generateToken();
        revokeAllUserResetTokens(user);

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(2, ChronoUnit.HOURS);

        ResetToken resetToken = ResetToken.builder()
                .user(user)
                .token(token)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .build();

        resetTokenRepository.save(resetToken);
        String resetUrl = frontendUrl + "/account/reset-password/" + token;
        authenticationEmailService.sendOtpEmail(
                user.getEmail(),
                null,
                EmailTemplateName.RESET_PASSWORD,
                resetUrl,
                null,
                "Password recovery");
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        ResetToken resetToken = resetTokenRepository.findByToken(resetPasswordRequest.getToken())
                .filter(token -> !token.isRedeemed() && !token.isRevoked() && !token.isExpired())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid password reset token."));
        if (resetToken.getExpiresAt().isBefore(Instant.now())){
            revokeAllUserResetTokens(resetToken.getUser());
            throw new InvalidResetTokenException("Password reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        revokeAllUserResetTokens(user);
        resetToken.setRedeemed(true);
        resetToken.setExpired(true);
        resetToken.setRevoked(true);
        resetToken.setValidatedAt(Instant.now());
        resetTokenRepository.save(resetToken);
    }

    private void revokeAllUserResetTokens(User user) {
        var validUserOtp = resetTokenRepository.findAllValidResetTokenByUser(user.getId());
        if (validUserOtp.isEmpty()) {
            return;
        }
        validUserOtp.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            token.setRedeemed(false);
        });
        resetTokenRepository.saveAll(validUserOtp);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest, User user) {
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid current password.");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Password are not the same.");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDTO getAuthUserDetails(User user) {
        Set<String> roles = user.getRoles()
                .stream()
                .map(Roles::getName)
                .collect(Collectors.toSet());
        return UserDTO.builder()
                .userId(user.getId())
                .username(user.getNickname())
                .email(user.getEmail())
                .role(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
