package com.stripe.payment_service_provider.subscription.service.impl;

import com.stripe.model.Price;
import com.stripe.model.Product;
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
import java.util.List;
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
                .map(plan -> dtoConverter.convertToDto(plan, PlanDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePlan(Product product) {
        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(product.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for product id: " + product.getId()));
        planRepository.delete(stripePlan);
    }

    @Transactional
    public void createOrUpdatePlan(Product product) {
        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(product.getId())
                .orElseGet(() -> StripePlan.builder().build());

        stripePlan.setStripeProductId(product.getId());
        stripePlan.setName(product.getName());
        stripePlan.setDescription(product.getDescription());
        stripePlan.setFeatures(Arrays.stream(product.getMarketingFeatures().toArray()).toList());
        stripePlan.setActive(true);
        StripePlan populatedStripePlan = populatePlanMetaData(stripePlan, product);

        planRepository.save(populatedStripePlan);
    }

    @Transactional
    public void createOrUpdatePrice(Price price) {
        StripePrice stripePrice = priceRepository.findStripePriceByStripePriceId(price.getId())
                .orElseGet(StripePrice::new);

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(price.getProduct())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found for product id: " + price.getProduct()));

        stripePrice.setStripePlan(stripePlan);
        stripePrice.setStripePriceId(price.getId());
        stripePrice.setAmount(price.getUnitAmountDecimal());
        stripePrice.setBillingCycle(BillingCycle.valueOf(price.getRecurring().getInterval().toUpperCase()));
        stripePrice.setActive(price.getActive());

        priceRepository.save(stripePrice);

    }

    private StripePlan populatePlanMetaData(StripePlan subscriptionStripePlan, Product product) {
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

}
