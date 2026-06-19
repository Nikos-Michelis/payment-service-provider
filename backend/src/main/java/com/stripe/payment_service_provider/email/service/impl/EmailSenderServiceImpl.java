package com.stripe.payment_service_provider.email.service.impl;

import com.stripe.payment_service_provider.email.EmailTemplateName;
import com.stripe.payment_service_provider.email.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Component
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.sender}")
    private String sender;

    private MimeMessageHelper MimeMessageSetup() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        return new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());
    }

    @Async
    @Override
    public void sendEmail(String to, String subject, EmailTemplateName emailTemplateName, Map<String, Object> properties) throws MessagingException {
        MimeMessageHelper helper = MimeMessageSetup();
        Context context = new Context();
        context.setVariables(properties);
        helper.setFrom(sender);
        helper.setTo(to);
        helper.setSubject(subject);
        String template = templateEngine.process(emailTemplateName.getName(), context);
        helper.setText(template, true);
        System.out.println(template);
        //javaMailSender.send(helper.getMimeMessage());
    }
}
