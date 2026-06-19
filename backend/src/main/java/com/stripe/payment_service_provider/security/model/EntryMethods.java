package com.stripe.payment_service_provider.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entry_methods")
@EqualsAndHashCode(of = "id",  callSuper = false)
public class EntryMethods extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Long id;
    @Column(name = "provider")
    private String provider;
    @ManyToMany(mappedBy = "entryMethods")
    @JsonIgnore
    private Set<User> users = new HashSet<>();
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void deleteSignUpMethods(EntryMethods entryMethods) {
        entryMethods.getUsers().removeIf(method -> method.getEntryMethods().remove(entryMethods));
        this.users.clear();
    }
}
