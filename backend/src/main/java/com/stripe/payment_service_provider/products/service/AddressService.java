package com.stripe.payment_service_provider.products.service;

import com.stripe.payment_service_provider.products.dto.AddressDTO;
import com.stripe.payment_service_provider.products.model.Address;
import com.stripe.payment_service_provider.user.model.User;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    List<AddressDTO> getUserAddresses(User user);
    AddressDTO createUserAddress(User user, AddressDTO request);
    AddressDTO updateUserAddress(User user, UUID addressUUID, AddressDTO request);
}
