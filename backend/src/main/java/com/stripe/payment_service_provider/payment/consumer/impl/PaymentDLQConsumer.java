package com.stripe.payment_service_provider.payment.consumer.impl;

import com.google.gson.Gson;
import com.stripe.model.Event;
import com.stripe.payment_service_provider.payment.webhook.model.EventStatus;
import com.stripe.payment_service_provider.payment.webhook.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentDLQConsumer {

    private final EventRepository eventRepository;

    @KafkaListener(
            topics = {
                    "stripe.subscriptions.dlq",
                    "stripe.products.dlq",
                    "stripe.payments.dlq",
                    "stripe.customers.dlq"
            },
            groupId = "stripe-dlq-processor"
    )
    public void handleDeadLetter(
            @Payload String payload,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String errorMessage,
            @Header(name = KafkaHeaders.DELIVERY_ATTEMPT, required = false) Integer deliveryAttempt,
            Acknowledgment ack) {
        Event event = new Gson().fromJson(payload, Event.class);
        log.info("Received deliveryAttempt: {}", deliveryAttempt);
        updateEventStatus(event.getId(), EventStatus.FAILED, errorMessage);
        ack.acknowledge();
    }

    private void updateEventStatus(String eventId, EventStatus status, String error) {
        eventRepository.findByStripeEventId(eventId).ifPresent(webhookEvent -> {
            webhookEvent.setStatus(status);
            webhookEvent.setRetryCount(webhookEvent.getRetryCount() + 1);
            if (error != null) webhookEvent.setErrorMessage(error);
            eventRepository.save(webhookEvent);
        });
    }
}