package com.stripe.payment_service_provider.products.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart")
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Long id;

    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    private Set<OrderLine> orderLines = new HashSet<>();

    public void addOrderLine(OrderLine orderLine) {
        this.orderLines.add(orderLine);
        orderLine.setCart(this);
    }

    public void removeOrderLine(OrderLine orderLine) {
        this.orderLines.remove(orderLine);
    }

}