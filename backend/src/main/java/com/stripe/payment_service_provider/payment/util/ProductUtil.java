package com.stripe.payment_service_provider.payment.util;

import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductUtil {

    public Product getProductBySubscription(Subscription subscription) throws StripeException {
        SubscriptionItem subscriptionItem = subscription.getItems().getData().getFirst();
        String productId = subscriptionItem.getPlan().getProduct();
        return Product.retrieve(productId);
    }

    public Product buildStripeProduct(StripePlan stripePlan, StripePrice stripePrice) {
        Product product = new Product();
        product.setId(stripePlan.getStripeProductId());
        product.setName(stripePlan.getName());
        Price price = new Price();
        price.setId(stripePrice.getStripePriceId());
        price.setUnitAmountDecimal(stripePrice.getAmount());
        product.setDefaultPriceObject(price);
        return product;
    }

    /*public Product buildOneOffProduct(Product product) {
        Product product = new Product();
        product.setId(product);
        product.setName(subscriptionPlan.getName());
        Price price = new Price();
        price.setId(subscriptionPlan.getStripePriceId());
        price.setUnitAmountDecimal(BigDecimal.valueOf(subscriptionPlan.getAmount()));
        product.setDefaultPriceObject(price);
        return product;
    }*/
}
