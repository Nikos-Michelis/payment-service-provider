package com.stripe.payment_service_provider.payment.workers;

import com.stripe.payment_service_provider.payment.producer.KafkaMessageProducer;
import com.stripe.payment_service_provider.payment.webhook.model.EventStatus;
import com.stripe.payment_service_provider.payment.webhook.model.WebhookEvent;
import com.stripe.payment_service_provider.payment.webhook.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRelayWorker {

    private final EventRepository eventRepository;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Transactional
    //@Scheduled(fixedDelayString = "3000")
    public void relayPendingToKafka() {
        List<WebhookEvent> webhookEvents = eventRepository.findTopNByStatusOrderByReceivedAt(EventStatus.PENDING, 50);
        if (webhookEvents.isEmpty()) {
            return;
        }

        for (WebhookEvent webhookEvent : webhookEvents) {
            WebhookEvent event = publishWebhookEvent(webhookEvent);
            eventRepository.save(event);
        }
    }

    @Transactional
   // @Scheduled(fixedDelayString = "3000")
    public void relayFailedToKafka() {
        List<WebhookEvent> webhookEvents = eventRepository.findTopNByStatusOrderByReceivedAt(EventStatus.FAILED, 50);
        if (webhookEvents.isEmpty()) {
            return;
        }

        for (WebhookEvent webhookEvent : webhookEvents) {
            WebhookEvent event = publishWebhookEvent(webhookEvent);
            eventRepository.save(event);
        }
    }

    private WebhookEvent publishWebhookEvent(WebhookEvent event) {
        try {
            kafkaMessageProducer.sendSync(event.getTopic(), event.getStripeEventId(), event.getRawPayload(), 5);
            event.setStatus(EventStatus.PUBLISHED);
            event.setPublishedAt(Instant.now());
            return event;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            event.setRetryCount(event.getRetryCount() + 1);
            if (event.getRetryCount() >= 5) {
                event.setStatus(EventStatus.FAILED);
                event.setErrorMessage(e.getMessage());
                log.error("Event [{}] failed after 5 retries", event.getStripeEventId());
            }
            return event;
        }
    }
}
