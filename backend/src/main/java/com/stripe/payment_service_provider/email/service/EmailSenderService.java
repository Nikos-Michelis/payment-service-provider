package com.stripe.payment_service_provider.email.service;

import com.stripe.payment_service_provider.email.EmailTemplateName;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface EmailSenderService {
    void sendEmail(String to, String subject, EmailTemplateName emailTemplateName, Map<String, Object> properties) throws MessagingException;
}
