package com.stripe.payment_service_provider.security.repository;

import com.stripe.payment_service_provider.user.model.Permission;
import com.stripe.payment_service_provider.user.model.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(Permissions name);
}
