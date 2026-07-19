package com.stripe.payment_service_provider.payment.consumer.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.payment_service_provider.payment.consumer.InvoiceEventHandler;
import com.stripe.payment_service_provider.payment.api.model.PaymentStatus;
import com.stripe.payment_service_provider.payment.api.service.PaymentMethodService;
import com.stripe.payment_service_provider.payment.consumer.PaymentIntentHandler;
import com.stripe.payment_service_provider.payment.consumer.SubscriptionEventHandler;
import com.stripe.payment_service_provider.payment.webhook.model.EventStatus;
import com.stripe.payment_service_provider.payment.webhook.repository.EventRepository;
import com.stripe.payment_service_provider.settings.exceptions.common.InvalidStateTransitionException;
import com.stripe.payment_service_provider.subscription.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final InvoiceEventHandler invoiceEventHandler;
    private final PaymentIntentHandler paymentIntentHandler;
    private final SubscriptionEventHandler subscriptionEventHandler;
    private final PaymentMethodService paymentMethodService;
    private final EventRepository eventRepository;
    private final PlanService planService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.topics.payments}",
            groupId = "stripe-payment-processor"
    )
    public void processPayments(@Payload String payload, Acknowledgment ack) throws StripeException {
        Event event = parsePayload(payload);
        switch (event.getType()) {
            case "payment_method.attached" -> paymentMethodService.addPaymentMethod(event);
            case "payment_method.detached" -> paymentMethodService.removePaymentMethod(event);
            case "payment_intent.created" -> paymentIntentHandler.onPaymentIntentCreate(event);
            case "payment_intent.processing" ->
                    paymentIntentHandler.onPaymentIntentUpdate(event, PaymentStatus.PROCESSING);
            case "payment_intent.succeeded" ->
                    paymentIntentHandler.onPaymentIntentUpdate(event, PaymentStatus.CAPTURED);
            case "payment_intent.canceled" ->
                    paymentIntentHandler.onPaymentIntentUpdate(event, PaymentStatus.CANCELED);
            case "payment_intent.failed" -> paymentIntentHandler.onPaymentIntentUpdate(event, PaymentStatus.FAILED);
            case "invoice.paid" -> invoiceEventHandler.onInvoicePaid(event);
            case "invoice.payment_failed" -> invoiceEventHandler.onInvoiceUpdate(event);
            case "invoice.upcoming" -> invoiceEventHandler.onInvoiceUpcoming(event);
            case "invoice_payment.paid" -> invoiceEventHandler.onInvoicePaymentPaid(event);
            default -> log.debug("Unhandled payment event: {}", event.getType());
        }

        updateEventStatus(event.getId());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.customers}",
            groupId = "stripe-payment-processor"
    )
    public void processSubscriptions(@Payload String payload, Acknowledgment ack) throws StripeException, InvalidStateTransitionException {
        Event event = parsePayload(payload);
        switch (event.getType()) {
            case "customer.subscription.created" -> subscriptionEventHandler.onSubscriptionCreate(event);
            case "customer.subscription.updated" -> subscriptionEventHandler.onSubscriptionUpdate(event);
            case "customer.subscription.deleted" -> subscriptionEventHandler.onSubscriptionCancel(event);
            default -> log.debug("Unhandled billing event: {}", event.getType());
        }

        updateEventStatus(event.getId());
        ack.acknowledge();
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.products}",
            groupId = "stripe-payment-processor"
    )
    public void processProducts(@Payload String payload, Acknowledgment ack) throws InvalidStateTransitionException {
        Event event = parsePayload(payload);
        switch (event.getType()) {
            case "product.created" -> planService.onPlanCreate(event);
            case "product.updated", "product.deleted" -> planService.onPlanUpdate(event);
            case "price.created" -> planService.onPriceCreate(event);
            case "price.updated" -> planService.onPriceUpdate(event);
            default -> log.debug("Unhandled billing event: {}", event.getType());
        }

        updateEventStatus(event.getId());
        ack.acknowledge();
        log.info("Processed billing event [{}] type [{}]", event.getId(), event.getType());
    }

    private Event parsePayload(String payload) {
        return objectMapper.readValue(payload, Event.class);
    }

    private void updateEventStatus(String eventId) {
        eventRepository.findByStripeEventId(eventId).ifPresent(webhookEvent -> {
            webhookEvent.setStatus(EventStatus.PROCESSED);
            webhookEvent.setProcessedAt(Instant.now());
            eventRepository.save(webhookEvent);
        });
    }
}