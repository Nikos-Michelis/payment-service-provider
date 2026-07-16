package com.stripe.payment_service_provider.subscription.service.impl;

import com.stripe.model.Event;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.payment_service_provider.settings.exceptions.common.ConflictException;
import com.stripe.payment_service_provider.subscription.model.BillingCycle;
import com.stripe.payment_service_provider.subscription.model.PlanType;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.subscription.dto.PlanDTO;
import com.stripe.payment_service_provider.subscription.repository.PriceRepository;
import com.stripe.payment_service_provider.subscription.service.PlanService;
import com.stripe.payment_service_provider.utils.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanRepository planRepository;
    private final PriceRepository priceRepository;
    private final DtoConverter dtoConverter;

    public List<PlanDTO> getAllPlans() {
        List<StripePlan> subscriptionStripePlan = planRepository.findAll();
        return subscriptionStripePlan
                .stream()
                .sorted(Comparator.comparing(StripePlan::getTokenLimit))
                .map(plan -> dtoConverter.convertToDto(plan, PlanDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void onPlanCreate(Event event) {
        Product product = handleProductEvent(event);
        Optional<StripePlan> stripePlan = planRepository.findSubscriptionPlanByStripeProductId(product.getId());
        if (stripePlan.isPresent()) {
            throw new ConflictException("Product already exists");
        }

        StripePlan newStripePlan = populatePlan(new StripePlan(), product);
        planRepository.save(newStripePlan);
    }

    @Transactional
    public void onPlanUpdate(Event event) {
        Product product = handleProductEvent(event);
        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for product id: " + product.getId()));

        StripePlan updatedStripePlan = populatePlan(stripePlan, product);
        planRepository.save(updatedStripePlan);
    }

    @Transactional
    public void onPriceCreate(Event event) {
        Price price = handlePriceEvent(event);
        Optional<StripePrice> stripePrice = priceRepository.findStripePriceByStripePriceId(price.getId());

        if (stripePrice.isPresent()) {
            throw new ConflictException("Price already exists");
        }

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(price.getProduct())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for product id: " + price.getProduct()));

        StripePrice newStripePrice = populatePrice(new StripePrice(), price);
        newStripePrice.setStripePlan(stripePlan);
        priceRepository.save(newStripePrice);
    }

    @Transactional
    public void onPriceUpdate(Event event) {
        Price price = handlePriceEvent(event);
        StripePrice stripePrice = priceRepository.findStripePriceByStripePriceId(price.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Price not found for price id: " + price.getId()));

        StripePrice newStripePrice = populatePrice(stripePrice, price);
        priceRepository.save(newStripePrice);
    }

    private StripePrice populatePrice(StripePrice stripePrice, Price price) {
        stripePrice.setStripePriceId(price.getId());
        stripePrice.setAmount(price.getUnitAmountDecimal());
        stripePrice.setCurrency(price.getCurrency());
        stripePrice.setBillingCycle(BillingCycle.valueOf(price.getRecurring().getInterval().toUpperCase()));
        stripePrice.setActive(price.getActive());
        return stripePrice;
    }

    private StripePlan populatePlan(StripePlan stripePlan, Product product) {
        stripePlan.setStripeProductId(product.getId());
        stripePlan.setName(product.getName());
        stripePlan.setDescription(product.getDescription());
        stripePlan.setFeatures(Arrays.stream(product.getMarketingFeatures().toArray()).toList());
        stripePlan.setActive(true);
        return populatePlanMetaData(stripePlan, product);
    }

    public StripePlan populatePlanMetaData(StripePlan subscriptionStripePlan, Product product) {
        if (product.getMetadata() == null) {
            throw new RuntimeException("Product metadata are missing");
        }

        Long interval = Long.parseLong(product.getMetadata().get("interval"));
        if (interval.describeConstable().isEmpty()) {
            throw new RuntimeException("Interval is missing");
        }
        subscriptionStripePlan.setTkResetHoursInterval(interval.intValue());

        Long tokenLimit = Long.parseLong(product.getMetadata().get("token_limit"));
        if (tokenLimit.describeConstable().isEmpty()) {
            throw new RuntimeException("token_limit is missing");
        }
        subscriptionStripePlan.setTokenLimit(tokenLimit.intValue());

        String plan = product.getMetadata().get("plan");
        if (tokenLimit.describeConstable().isEmpty()) {
            throw new RuntimeException("plan is missing");
        }
        PlanType planType = PlanType.valueOf(plan.toUpperCase());
        subscriptionStripePlan.setPlanType(planType);
        return subscriptionStripePlan;
    }

    private Price handlePriceEvent(Event event) {
        return (Price) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
    }

    private Product handleProductEvent(Event event) {
        return (Product) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

}
