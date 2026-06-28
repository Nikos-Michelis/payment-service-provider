package com.stripe.payment_service_provider.payment.producer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "webhook_event")
public class WebhookEvent {
    @Id
    @Column(name = "webhook_event_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_event_id", nullable = false)
    private String stripeEventId;

    @Size(max = 100)
    @NotNull
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Size(max = 100)
    @NotNull
    @Column(name = "topic", nullable = false, length = 100)
    private String topic;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "json", nullable = false)
    private Object rawPayload;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'PENDING'")
    @Column(name = "status", nullable = false, length = 20)
    private EventStatus status;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Size(max = 500)
    @Column(name = "error_message", length = 500)
    private String errorMessage;


}