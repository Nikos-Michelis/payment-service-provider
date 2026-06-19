package com.stripe.payment_service_provider.user.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contact")
@EqualsAndHashCode(of = "id", callSuper = false)
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Column(name = "email", nullable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;
    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;
}
