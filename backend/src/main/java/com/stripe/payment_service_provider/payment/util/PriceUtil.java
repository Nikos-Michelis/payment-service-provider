package com.stripe.payment_service_provider.payment.util;

import com.stripe.payment_service_provider.subscription.model.StripePrice;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PriceUtil {
    public StripePrice getPriceFromPlanAvailablePrices(Set<StripePrice> stripePrices, String priceId) {
        return stripePrices.stream()
                .filter(price -> price.getStripePriceId().equals(priceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
    }


}
