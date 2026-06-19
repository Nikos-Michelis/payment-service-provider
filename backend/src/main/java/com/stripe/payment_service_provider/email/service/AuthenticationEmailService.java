package com.stripe.payment_service_provider.email.service;

import com.stripe.payment_service_provider.email.EmailTemplateName;
import jakarta.mail.MessagingException;

public interface AuthenticationEmailService {
    void sendOtpEmail(String to, String username, EmailTemplateName emailTemplate, String url, String activationCode, String subject) throws MessagingException, jakarta.mail.MessagingException;
}
