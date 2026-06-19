package com.stripe.payment_service_provider.user.controller;

import com.stripe.payment_service_provider.security.limiter.RateLimited;
import com.stripe.payment_service_provider.user.dto.response.ResponseDTO;
import com.stripe.payment_service_provider.user.services.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_DEVELOPER', 'ROLE_ADMIN', 'ROLE_MODERATOR')")
@Tag(name = "Dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/report/members")
    @PreAuthorize("hasAnyAuthority('developer:read', 'admin:read', 'moderator:read')")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<?> getAllMembers() {
        return ResponseEntity.ok().
                body(ResponseDTO
                        .builder()
                        .timestamp(Instant.now())
                        .data(dashboardService.getAllMembers())
                        .build());
    }
}
