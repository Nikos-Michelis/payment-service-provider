package com.stripe.payment_service_provider.payment.api.mappers;

import com.stripe.model.Subscription;
import com.stripe.payment_service_provider.payment.api.dto.SubscriptionDTO;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;

@Mapper
public interface StripeSubscriptionObjectMapper {

    @Mapping(target = "subscriptionId", source = "id")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    @Mapping(target = "startDate", source = "startDate", qualifiedByName = "epochToInstant")
    @Mapping(target = "endedAt", source = "endedAt", qualifiedByName = "epochToInstant")
    SubscriptionDTO toDto(Subscription subscription);

    @Named("mapStatus")
    default SubscriptionStatus mapStatus(String status) {
        return SubscriptionStatus.valueOf(status.toUpperCase());
    }

    @Named("epochToInstant")
    default Instant epochToInstant(Long epochSeconds) {
        return epochSeconds == null ? null : Instant.ofEpochSecond(epochSeconds);
    }
}