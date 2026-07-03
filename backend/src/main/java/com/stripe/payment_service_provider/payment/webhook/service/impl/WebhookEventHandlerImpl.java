package com.stripe.payment_service_provider.payment.webhook.service.impl;

import com.stripe.payment_service_provider.payment.webhook.model.EventStatus;
import com.stripe.payment_service_provider.payment.webhook.model.WebhookEvent;
import com.stripe.payment_service_provider.payment.webhook.repository.EventRepository;
import com.stripe.payment_service_provider.payment.webhook.service.StripeWebhookHandler;
import com.stripe.model.*;
import com.stripe.payment_service_provider.settings.exceptions.common.ConflictException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookEventHandlerImpl implements StripeWebhookHandler {

    private final EventRepository eventRepository;

    @Value("${spring.kafka.topics.products}")
    private String productsTopic;
    @Value("${spring.kafka.topics.customers}")
    private String customersTopic;
    @Value("${spring.kafka.topics.payments}")
    private String paymentsTopic;
    private Map<String, String> topicMappings;

    @PostConstruct
    void init() {
        topicMappings = Map.ofEntries(
                Map.entry("invoice.", paymentsTopic),
                Map.entry("payment_intent.", paymentsTopic),
                Map.entry("charge.", paymentsTopic),
                Map.entry("payment_method.", paymentsTopic),
                Map.entry("product.", productsTopic),
                Map.entry("price.", productsTopic),
                Map.entry("customer.", customersTopic)
        );
    }

    @Override
    @Transactional
    public void saveEvent(Event event) {

        if (eventRepository.existsAllByStripeEventId(event.getId())) {
            throw new ConflictException("Stripe event with id " + event.getId() + " already exists");
        }

        String topic = resolveTopic(event.getType());
        String rawJson = event.getRawJsonObject().toString();

        WebhookEvent webhookEvent = buildWebhookEvent(event, topic, rawJson);
        eventRepository.save(webhookEvent);
    }

    private String resolveTopic(String eventType) {
        return topicMappings.entrySet().stream()
                .filter(entry -> eventType.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(paymentsTopic);
    }

    private WebhookEvent buildWebhookEvent(Event event, String topic, String rawJson) {
        return WebhookEvent.builder()
                    .stripeEventId(event.getId())
                    .eventType(event.getType())
                    .topic(topic)
                    .rawPayload(rawJson)
                    .status(EventStatus.PENDING)
                    .retryCount(0)
                    .receivedAt(Instant.now())
                    .build();
    }
}