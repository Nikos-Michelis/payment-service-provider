package com.stripe.payment_service_provider.products.mappers;

import com.stripe.payment_service_provider.products.dto.AddressDTO;
import com.stripe.payment_service_provider.products.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    AddressDTO toDTO(Address address);
    List<AddressDTO> toDTO(List<Address> addresses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    Address toEntity(AddressDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    void updateEntity(AddressDTO request, @MappingTarget Address address);
}
