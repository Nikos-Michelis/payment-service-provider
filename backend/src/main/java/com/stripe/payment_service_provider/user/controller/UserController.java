package com.stripe.payment_service_provider.user.controller;

import com.stripe.payment_service_provider.security.services.cookie.CookieServiceProvider;
import com.stripe.payment_service_provider.user.dto.UserDTO;
import com.stripe.payment_service_provider.security.dto.request.ChangePasswordRequest;
import com.stripe.payment_service_provider.security.limiter.RateLimited;
import com.stripe.payment_service_provider.security.services.AuthenticationService;
import com.stripe.payment_service_provider.user.dto.response.ResponseDTO;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.user.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "user")
@Slf4j
public class UserController {

    @Value("${application.backend.url}")
    private String backendUrl;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CookieServiceProvider cookieServiceProvider;
    private final int MAX_ITEMS = 50;

    @PostMapping("/change-password")
    @RateLimited(requests = 5, durationSeconds = 60)
    public ResponseEntity<?> changePassword(@AuthenticationPrincipal User user,
                                            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest, user);
        Map<String, String> map = new HashMap<>();
        map.put("message", "Password successfully changed.");
        return ResponseEntity.ok().body(map);
    }

    @DeleteMapping("/account/deactivate")
    @RateLimited(requests = 2, durationSeconds = 60)
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal User user){
        authenticationService.deleteAccount(user);
        ResponseCookie refreshTokenCookie = cookieServiceProvider.clearRefreshTokenCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ResponseDTO.builder()
                        .timestamp(Instant.now())
                        .message("Your account has been disabled and will be permanently deleted after 30 days.")
                        .build());
    }

    @GetMapping("/my-account")
    @RateLimited(requests = 100, durationSeconds = 60)
    public ResponseEntity<UserDTO> myAccount(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.getAuthUserDetails(user));
    }
}
