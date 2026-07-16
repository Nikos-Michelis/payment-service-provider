package com.stripe.payment_service_provider.products.model;

import com.stripe.payment_service_provider.auditing.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "shippers")
public class Shipper extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 36)
    @NotNull
    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ShippingMethod type;

    @Size(max = 255)
    @Column(name = "website")
    private String website;

    @Size(max = 500)
    @Column(name = "tracking_url", length = 500)
    private String trackingUrl;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "active", nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "shipper")
    private Set<OrderHasShipment> orderHasShipments = new HashSet<>();

    @OneToMany(mappedBy = "shipper")
    private Set<ShipperHasCountry> shipperHasCountries = new HashSet<>();
}