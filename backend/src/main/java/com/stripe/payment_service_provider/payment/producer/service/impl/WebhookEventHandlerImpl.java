package com.stripe.payment_service_provider.payment.producer.service.impl;

import com.stripe.payment_service_provider.payment.producer.model.EventStatus;
import com.stripe.payment_service_provider.payment.producer.model.WebhookEvent;
import com.stripe.payment_service_provider.payment.producer.repository.EventRepository;
import com.stripe.payment_service_provider.payment.producer.service.StripeWebhookHandler;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookEventHandlerImpl implements StripeWebhookHandler {

    private final EventRepository eventRepository;

    @Value("${kafka.topics.billing}")
    private String billingTopic;

    @Value("${kafka.topics.customers}")
    private String customersTopic;

    @Value("${kafka.topics.payments}")
    private String paymentsTopic;

    @Override
    @Transactional
    public void handleStripeEvent(Event event) {

        if (eventRepository.existsAllByStripeEventId(event.getId())) {
            log.info("Duplicate Stripe event ignored: {}", event.getId());
            return;
        }

        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setStripeEventId(event.getId());
        webhookEvent.setEventType(event.getType());
        webhookEvent.setTopic(resolveTopic(event.getType()));
        webhookEvent.setRawPayload(event.getRawJsonObject());
        webhookEvent.setStatus(EventStatus.PENDING);
        webhookEvent.setRetryCount(0);
        webhookEvent.setReceivedAt(Instant.now());
        eventRepository.save(webhookEvent);
        eventRepository.save(webhookEvent);
    }

    private String resolveTopic(String eventType) {
        if (eventType.startsWith("invoice.") ||
                eventType.startsWith("customer.subscription.")) {
            return billingTopic;
        }

        if (eventType.startsWith("customer.") ||
                eventType.startsWith("payment_method.") ||
                eventType.startsWith("product.") ||
                eventType.startsWith("price.")) {
            return customersTopic;
        }

        if (eventType.startsWith("payment_intent.") || eventType.startsWith("charge.")) {
            return paymentsTopic;
        }

        log.warn("No topic mapped for event type: {} — using billing as fallback", eventType);
        return billingTopic;
    }
}