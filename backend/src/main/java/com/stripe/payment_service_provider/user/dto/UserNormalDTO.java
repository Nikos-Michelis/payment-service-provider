package com.stripe.payment_service_provider.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stripe.payment_service_provider.utils.DTOEntity;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserNormalDTO implements DTOEntity {
    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;
    @JsonProperty("accountNonLocked")
    private boolean accountNonLocked;
    @JsonProperty("accountNonExpired")
    private boolean accountNonExpired;
    @JsonProperty("credentialsNonExpired")
    private boolean credentialsNonExpired;
    @JsonProperty("enabled")
    private boolean enabled;
    @JsonProperty("credentialsExpiryDate")
    private Instant credentialsExpiryDate;
    @JsonProperty("accountExpiryDate")
    private Instant accountExpiryDate;
    @JsonProperty("role")
    private Set<String> role;
    @JsonProperty("createdAt")
    private Instant createdAt;
    @JsonProperty("updatedAt")
    private Instant updatedAt;
}
