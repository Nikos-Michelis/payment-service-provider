package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.ShippingRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShipperRatesRepository extends JpaRepository<ShippingRates, Long> {
    @Query("""
        SELECT sr
        FROM ShippingRates sr
        INNER JOIN sr.shipper s
        INNER JOIN sr.country c
        WHERE s.uuid = :shipperUUID AND c.code = :countryCode
        """)
    Optional<ShippingRates> findShipperByUuidAndCountryCode(@Param("shipperUUID") String shipperUUID, @Param("countryCode") String countryCode);
}
