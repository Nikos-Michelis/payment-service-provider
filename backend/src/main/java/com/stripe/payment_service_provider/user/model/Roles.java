package com.stripe.payment_service_provider.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@EqualsAndHashCode(of = "id", callSuper = false)
public class Roles extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @BatchSize(size = 10)
    private Set<Permission> permissions;
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    public void deleteRoles(Roles roles) {
        roles.getUsers().removeIf(method -> method.getRoles().remove(roles));
        this.users.clear();
    }
}
