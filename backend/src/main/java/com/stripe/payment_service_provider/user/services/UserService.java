package com.stripe.payment_service_provider.user.services;

import com.stripe.payment_service_provider.security.dto.request.ChangePasswordRequest;
import com.stripe.payment_service_provider.security.dto.request.ResetCredentialsRequest;
import com.stripe.payment_service_provider.security.dto.request.ResetPasswordRequest;
import com.stripe.payment_service_provider.user.dto.UserDTO;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.mail.MessagingException;

public interface UserService {
    void generatePasswordResetToken(ResetCredentialsRequest resetCredentialsRequest) throws MessagingException;
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
    void changePassword(ChangePasswordRequest changePasswordRequest, User user);
    UserDTO getAuthUserDetails(User user);
}
