package com.stripe.payment_service_provider.payment.webhook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "webhook_event")
public class WebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webhook_event_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "stripe_event_id", nullable = false)
    private String stripeEventId;

    @Column(name = "stripe_customer_id", nullable = false)
    private String customerId;

    @Size(max = 100)
    @NotNull
    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Size(max = 100)
    @NotNull
    @Column(name = "topic", nullable = false, length = 100)
    private String topic;

    @NotNull
    @Column(name = "raw_payload", columnDefinition = "TEXT", nullable = false)
    private String rawPayload;

    @NotNull
    @Enumerated(EnumType.STRING)
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
    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

}