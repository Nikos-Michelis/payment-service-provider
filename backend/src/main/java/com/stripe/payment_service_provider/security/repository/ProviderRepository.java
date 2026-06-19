package com.stripe.payment_service_provider.security.repository;

import com.stripe.payment_service_provider.security.model.EntryMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<EntryMethods, Long> {
    Optional<EntryMethods> findByProvider(String provider);
}
