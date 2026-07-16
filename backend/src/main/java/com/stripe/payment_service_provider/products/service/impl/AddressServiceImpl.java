package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.dto.AddressDTO;
import com.stripe.payment_service_provider.products.mappers.AddressMapper;
import com.stripe.payment_service_provider.products.model.Address;
import com.stripe.payment_service_provider.products.repository.AddressRepository;
import com.stripe.payment_service_provider.products.service.AddressService;
import com.stripe.payment_service_provider.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = addressRepository.findAllByUser(user);
        return addressMapper.toDTO(addresses);
    }

    @Transactional
    @Override
    public AddressDTO  createUserAddress(User user, AddressDTO request) {
        Address address = addressMapper.toEntity(request);
        address.setUser(user);
        Address savedAddress =  addressRepository.save(address);
        return addressMapper.toDTO(savedAddress);
    }

    @Transactional
    @Override
    public AddressDTO updateUserAddress(User user, UUID addressUUID, AddressDTO request) {
        Address address = addressRepository.findAddressByUuidAndUser_id(addressUUID, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressMapper.updateEntity(request, address);
        Address updatedAddress = addressRepository.save(address);
        return addressMapper.toDTO(updatedAddress);
    }
}
