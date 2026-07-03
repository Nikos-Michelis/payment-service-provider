package com.stripe.payment_service_provider.configuration.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaDLQConfig {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public DefaultErrorHandler errorHandler() {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition(record.topic() + ".dlq", record.partition())
        );

        recoverer.setHeadersFunction((record, ex) -> {
            RecordHeaders headers = new RecordHeaders();

            headers.add(new RecordHeader(
                    "dlt-exception-message",
                    (ex.getMessage() != null ? ex.getMessage() : "unknown")
                            .getBytes(StandardCharsets.UTF_8)));

            headers.add(new RecordHeader(
                    "dlt-exception-class",
                    ex.getClass().getName().getBytes(StandardCharsets.UTF_8)));

            headers.add(new RecordHeader(
                    "dlt-original-topic",
                    record.topic().getBytes(StandardCharsets.UTF_8)));

            headers.add(new RecordHeader(
                    "dlt-failed-at",
                    Instant.now().toString().getBytes(StandardCharsets.UTF_8)));

            return headers;
        });

        FixedBackOff backOff = new FixedBackOff(2000L, 5L);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions();

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Retry attempt [{}/5] for record [{}]: {}",
                        deliveryAttempt, record.key(), ex.getCause().getMessage())
        );

        return errorHandler;
    }
}