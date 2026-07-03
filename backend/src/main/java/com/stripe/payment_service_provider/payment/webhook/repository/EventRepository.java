package com.stripe.payment_service_provider.payment.webhook.repository;

import com.stripe.payment_service_provider.payment.webhook.model.EventStatus;
import com.stripe.payment_service_provider.payment.webhook.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.LinkedList;
import java.util.Optional;

public interface EventRepository extends JpaRepository<WebhookEvent, Long> {
    boolean existsAllByStripeEventId(String eventId);
    LinkedList<WebhookEvent> findTopNByStatusOrderByReceivedAt(EventStatus status, Integer count);
    Optional<WebhookEvent> findByStripeEventId(String eventId);
}
