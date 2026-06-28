package com.stripe.payment_service_provider.payment.producer.repository;

import com.stripe.payment_service_provider.payment.producer.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<WebhookEvent, Long> {
    boolean existsAllByStripeEventId(String eventId);
}
