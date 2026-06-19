package com.stripe.payment_service_provider.security.repository;

import com.stripe.payment_service_provider.user.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String role);
}
