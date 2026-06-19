package com.stripe.payment_service_provider.user.services;

import com.stripe.payment_service_provider.user.dto.UserDTO;

import java.util.List;

public interface DashboardService {
    List<UserDTO> getAllMembers();
}
