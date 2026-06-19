package com.stripe.payment_service_provider.user.services.impl;

import com.stripe.payment_service_provider.configuration.utils.CacheNames;
import com.stripe.payment_service_provider.user.dto.UserDTO;
import com.stripe.payment_service_provider.user.model.Roles;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.user.reporitory.UserRepository;
import com.stripe.payment_service_provider.user.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    @Autowired
    public DashboardServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Cacheable(value = CacheNames.MEMBERS_CACHE, key = "'members'", sync = true)
    public List<UserDTO> getAllMembers() {
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(member -> UserDTO.builder()
                        .username(member.getNickname())
                        .email(member.getEmail())
                        .role(member.getRoles().stream().map(Roles::getName).collect(Collectors.toSet()))
                        .accountNonLocked(member.isAccountNonLocked())
                        .enabled(member.isEnabled())
                        .createdAt(member.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
