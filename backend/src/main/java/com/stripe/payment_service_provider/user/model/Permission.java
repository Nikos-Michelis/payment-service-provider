package com.stripe.payment_service_provider.user.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.UuidGenerator;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "permissions")
@EqualsAndHashCode(of = "id",  callSuper = false)
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;
    @Column(name = "name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private Permissions name;
    @Column(name = "description")
    private String description;
    @ManyToMany(mappedBy = "permissions")
    @JsonIgnore
    @BatchSize(size = 10)
    private Set<Roles> roles;
}