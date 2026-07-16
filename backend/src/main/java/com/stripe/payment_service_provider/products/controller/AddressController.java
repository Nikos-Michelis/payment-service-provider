package com.stripe.payment_service_provider.products.controller;
import com.stripe.payment_service_provider.products.dto.AddressDTO;
import com.stripe.payment_service_provider.products.service.AddressService;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/address")
    public ResponseEntity<?> getAllUserAddresses(@AuthenticationPrincipal User user) {
        List<AddressDTO> addresses = addressService.getUserAddresses(user);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/address")
    public ResponseEntity<?> createUserAddress(@AuthenticationPrincipal User user, @RequestBody @Valid AddressDTO addressDTO) {
        AddressDTO address = addressService.createUserAddress(user, addressDTO);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/address/{uuid}")
    public ResponseEntity<?> updateUserAddress(
            @AuthenticationPrincipal User user,
            UUID addressUUID,
            @RequestBody @Valid AddressDTO addressDTO
    ) {
        AddressDTO address = addressService.updateUserAddress(user, addressUUID, addressDTO);
        return ResponseEntity.ok(address);
    }
}
