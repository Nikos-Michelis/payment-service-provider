package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    @Query("""
        SELECT s
        FROM Shipper s
        INNER JOIN s.shipperHasCountries sc
        INNER JOIN sc.country c
        WHERE s.uuid = :shipperUUID
        """)
    Optional<Shipper> findAllShipperSupportedCountries(@Param("shipperUUID") String shipperUUID);
}
