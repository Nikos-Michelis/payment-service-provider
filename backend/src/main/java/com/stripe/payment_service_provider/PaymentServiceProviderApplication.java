package com.stripe.payment_service_provider;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.PriceCollection;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.param.PriceListParams;
import com.stripe.param.ProductListParams;
import com.stripe.payment_service_provider.security.repository.PermissionRepository;
import com.stripe.payment_service_provider.security.repository.RoleRepository;
import com.stripe.payment_service_provider.subscription.model.BillingCycle;
import com.stripe.payment_service_provider.subscription.model.PlanType;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.subscription.repository.PriceRepository;
import com.stripe.payment_service_provider.user.model.Permission;
import com.stripe.payment_service_provider.user.model.Permissions;
import com.stripe.payment_service_provider.user.model.Role;
import com.stripe.payment_service_provider.user.model.Roles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableKafka
public class PaymentServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceProviderApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedRolesAndPermissions(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        return args -> {
            for (Permissions permissionEnum : Permissions.values()) {
                permissionRepository.findByName(permissionEnum).orElseGet(() -> {
                    Permission permission = Permission.builder()
                            .name(permissionEnum)
                            .description(permissionEnum.getPermission())
                            .build();
                    return permissionRepository.save(permission);
                });
            }
            for (Role roleEnum : Role.values()) {
                roleRepository.findByName(roleEnum.name()).orElseGet(() -> {
                    Set<Permission> permissions = roleEnum.getPermissions().stream()
                            .map(permissionEnum -> permissionRepository.findByName(permissionEnum)
                                    .orElseThrow(() -> new RuntimeException(
                                            "Permission not found: " + permissionEnum.name())))
                            .collect(Collectors.toSet());

                    Roles newRole = Roles.builder()
                            .name(roleEnum.name())
                            .permissions(permissions)
                            .build();
                    return roleRepository.save(newRole);
                });
            }
        };
    }

    @Bean
    public CommandLineRunner seedStripePlansAndPrices(PlanRepository planRepository, PriceRepository priceRepository) {
        return args -> {
            ProductCollection products = getProductCollection();
            for (var product : products.autoPagingIterable()) {
                StripePlan plan = planRepository
                        .findSubscriptionPlanByStripeProductId(product.getId())
                        .orElseGet(() -> {
                            StripePlan basePlan = StripePlan.builder()
                                    .stripeProductId(product.getId())
                                    .name(product.getName())
                                    .description(product.getDescription())
                                    .active(product.getActive())
                                    .features(Arrays.stream(product.getMarketingFeatures().toArray()).toList())
                                    .build();
                            StripePlan newPlan = populatePlanMetaData(basePlan, product);
                            return planRepository.save(newPlan);
                        });

                PriceCollection prices = getPriceCollection(product.getId());
                for (var price : prices.autoPagingIterable()) {
                    priceRepository.findStripePriceByStripePriceId(price.getId())
                            .orElseGet(() -> {
                                StripePrice newStripePrice = StripePrice.builder()
                                        .stripePriceId(price.getId())
                                        .amount(price.getUnitAmountDecimal())
                                        .currency(price.getCurrency())
                                        .billingCycle(BillingCycle.valueOf(price.getRecurring().getInterval().toUpperCase()))
                                        .stripePlan(plan)
                                        .active(price.getActive())
                                        .build();
                                    return priceRepository.save(newStripePrice);
                            });
                }
            }
        };
    }

    private ProductCollection getProductCollection() {
        try {

            ProductListParams productParams = ProductListParams.builder()
                    .setActive(true)
                    .build();

            return Product.list(productParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }

    private PriceCollection getPriceCollection(String planId) {
        try {

            PriceListParams priceParams = PriceListParams.builder()
                    .setProduct(planId)
                    .setActive(true)
                    .build();

            return Price.list(priceParams);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
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
}
