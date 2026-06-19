package com.stripe.payment_service_provider.email.service.impl;

import com.stripe.payment_service_provider.email.EmailTemplateName;
import com.stripe.payment_service_provider.email.service.AuthenticationEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationEmailServiceImpl implements AuthenticationEmailService {
    private final EmailSenderServiceImpl emailSenderService;

    @Override
    public void sendOtpEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String url,
            String activationCode,
            String subject
    ) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("url", url);
        properties.put("code", activationCode);
        emailSenderService.sendEmail(to, subject, emailTemplate, properties);
    }
}
