package com.stripe.payment_service_provider.payment.util;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class ProductUtil {

    public SubscriptionItem getSubscriptionItem(Subscription subscription) {
        return subscription.getItems().getData().getFirst();
    }

    public StripePrice getPriceFromPlanAvailablePrices(Set<StripePrice> stripePrices, String priceId) {
        return stripePrices.stream()
                .filter(price -> price.getStripePriceId().equals(priceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
    }

    public Product buildStripePlan(StripePlan stripePlan, StripePrice stripePrice) {
        Product product = new Product();
        product.setId(stripePlan.getStripeProductId());
        product.setName(stripePlan.getName());
        Price price = new Price();
        price.setId(stripePrice.getStripePriceId());
        price.setUnitAmountDecimal(stripePrice.getAmount());
        product.setDefaultPriceObject(price);
        return product;
    }
}
