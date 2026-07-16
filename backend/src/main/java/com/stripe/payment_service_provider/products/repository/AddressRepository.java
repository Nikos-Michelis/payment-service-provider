package com.stripe.payment_service_provider.products.repository;

import com.stripe.payment_service_provider.products.model.Address;
import com.stripe.payment_service_provider.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser(User user);
    Optional<Address> findAddressByUuidAndUser_id(UUID addressUUID, Long userId);
}
