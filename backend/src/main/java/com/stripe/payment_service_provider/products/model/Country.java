package com.stripe.payment_service_provider.products.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "countries")
public class Country extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Size(max = 2)
    @NotNull
    @Column(name = "code", nullable = false, length = 2)
    private String code;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "fromCountry")
    private Set<OrderHasShipment> orderHasShipments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "country")
    private Set<ShipperHasCountry> shipperHasCountries = new LinkedHashSet<>();


}