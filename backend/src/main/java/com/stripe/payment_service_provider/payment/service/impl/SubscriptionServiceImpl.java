package com.stripe.payment_service_provider.payment.service.impl;

import com.stripe.model.Product;
import com.stripe.payment_service_provider.payment.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.dto.payment.PaymentResponseDTO;
import com.stripe.payment_service_provider.payment.dto.payment.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.dto.payment.SubscriptionResponseDTO;
import com.stripe.payment_service_provider.payment.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.model.StripePaymentMethod;
import com.stripe.payment_service_provider.payment.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.repository.StripeCustomerRepository;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.payment.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.payment.util.*;
import com.stripe.payment_service_provider.settings.exceptions.auth.SessionNotFoundException;
import com.stripe.payment_service_provider.settings.exceptions.stripe.CustomerNotFoundException;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.dto.payment.PaymentRequestDTO;
import com.stripe.payment_service_provider.payment.dto.StripeSessionDTO;
import com.stripe.payment_service_provider.payment.service.StripeSubscriptionService;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.settings.exceptions.subscription.SubscriptionConflictException;
import com.stripe.payment_service_provider.settings.exceptions.subscription.SubscriptionNotFoundException;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.SubscriptionItemListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.billingportal.SessionCreateParams.*;
import com.stripe.param.billingportal.SessionCreateParams.FlowData.SubscriptionUpdateConfirm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements StripeSubscriptionService {
    private final SubscriptionUtil subscriptionUtil;
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final PortalSessionUtil portalSessionUtil;
    private final UserDetailsService userDetailsService;
    private final CustomerUtil customerUtil;
    private final ProductUtil productUtil;
    private final PlanRepository planRepository;
    private final CustomerPortalRepository customerPortalRepository;
    private final StripeCustomerRepository stripeCustomerRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    @Value("${application.api.stripe.tier.trial.period}")
    private Long trialPeriod;
    @Value("${application.api.stripe.hosted.configs.update-id}")
    private String UPDATE_PORTAL_CONFIG;
    @Value("${application.api.stripe.hosted.configs.cancel-id}")
    private String CANCEL_PORTAL_CONFIG;

    @Override
    public String renewSubscription(String email, String idempotencyKey) throws StripeException {
        Customer stripeCustomer = customerUtil.findCustomerByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        Subscription subscription = subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomer.getId(), EnumSet.of(SubscriptionStatus.PAST_DUE, SubscriptionStatus.UNPAID))
                .orElseThrow(() -> new SubscriptionNotFoundException("User " + stripeCustomer.getId() + " does not have any expired subscription."));

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Invoice latestInvoice = Invoice.retrieve(subscription.getLatestInvoice(), requestOptions);

        return latestInvoice.getHostedInvoiceUrl();
    }

    @Override
    @Transactional
    public SessionResponseDTO createSubscription(PaymentRequestDTO paymentRequest, String email, String idempotencyKey) throws StripeException {
        Optional<CustomerPortal> customerPortal = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (customerPortal.isPresent()) {
            return new SessionResponseDTO(customerPortal.get().getSessionId(), customerPortal.get().getSessionUrl(), customerPortal.get().getCreatedAt());
        }

        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(email);
        String stripeCustomerId = stripeCustomer.getStripeCustomerId();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(paymentRequest.productId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Not found any plan with the provided id " +  paymentRequest.productId()));

        StripePrice stripePrice = getPriceFromPlanAvailablePrices(stripePlan.getPrices(), paymentRequest.productId());

        Optional<Subscription> subscription = subscriptionUtil.getSubscriptionByStatusAndCustomer(
                stripeCustomer.getStripeCustomerId(), EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));

        if (subscription.isPresent()) {
            throw new SubscriptionConflictException("User already has an active subscription.");
        }

        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(SessionCreateParams.Mode.SUBSCRIPTION, stripeCustomerId);
        Product product = productUtil.buildStripeProduct(stripePlan, stripePrice);

        SessionCreateParams.LineItem lineItem = subscriptionUtil.buildSubscriptionLineItem(product);
        paramsBuilder.addLineItem(lineItem);
        SessionCreateParams sessionCreateParams = paramsBuilder.build();

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Session session = checkoutSessionUtil.createCheckoutSession(sessionCreateParams, requestOptions);

        StripeSessionDTO stripeSessionDTO = new StripeSessionDTO(session.getId(), session.getUrl(), session.getObject());
        saveStripeSession(stripeCustomer, stripeSessionDTO, idempotencyKey);

        return new SessionResponseDTO(session.getId(), session.getUrl(), Instant.ofEpochSecond(session.getCreated()));
    }

    @Override
    public SessionResponseDTO updateSubscription(PaymentRequestDTO paymentRequest, String email, String idempotencyKey) throws StripeException {
        Optional<CustomerPortal> customerPortal = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (customerPortal.isPresent()) {
            return new SessionResponseDTO(customerPortal.get().getSessionId(), customerPortal.get().getSessionUrl(), customerPortal.get().getCreatedAt());
        }

        User user = (User) userDetailsService.loadUserByUsername(email);
        String stripeCustomerId = user.getStripeCustomer().getStripeCustomerId();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(paymentRequest.productId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Not found any plan with the provided id " +  paymentRequest.productId()));

        Subscription subscription =
                subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomerId, EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                        .orElseThrow(() -> new SubscriptionConflictException("User does not have any active subscription."));
        String subscriptionItemId = subscription.getItems().getData().getFirst().getId();

        StripePrice stripePrice = getPriceFromPlanAvailablePrices(stripePlan.getPrices(), paymentRequest.productId());

        SubscriptionUpdateConfirm subscriptionUpdateConfirm =
                portalSessionUtil.getSubscriptionUpdateConfirm(subscription.getId(), subscriptionItemId, stripePrice.getStripePriceId());

        Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomerId);
        paramsBuilder.setConfiguration(UPDATE_PORTAL_CONFIG);
        paramsBuilder.setFlowData(
                portalSessionUtil.buildPortalSessionFlow(FlowData.Type.SUBSCRIPTION_UPDATE_CONFIRM, "http://localhost:3000/billing/success")
                        .setSubscriptionUpdateConfirm(subscriptionUpdateConfirm)
                        .build()
        );

        RequestOptions requestOptions = portalSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        StripeSessionDTO stripeSessionDTO = new StripeSessionDTO(session.getId(), session.getUrl(), session.getObject());
        saveStripeSession(user.getStripeCustomer(), stripeSessionDTO, idempotencyKey);

        return new SessionResponseDTO(session.getId(), session.getUrl(), Instant.ofEpochSecond(session.getCreated()));
    }

    @Override
    public Optional<List<SubscriptionResponseDTO>> findSubscriptionByCustomerEmail(String email) throws StripeException {

        StripeCustomer stripeCustomer = stripeCustomerRepository.findStripeCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not have any active subscription."));

        Subscription subscription =
                subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomer.getStripeCustomerId(), EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                        .orElseThrow(() -> new ResourceNotFoundException("User does not have any active subscription."));

        SubscriptionItemListParams subscriptionItemListParams = subscriptionUtil.getSubscriptionItemListParams(subscription);
        SubscriptionItemCollection subscriptionItemCollection = subscriptionUtil.getSubscriptionItemCollection(subscriptionItemListParams);

        List<SubscriptionResponseDTO> response = new ArrayList<>();
        for (SubscriptionItem item : subscriptionItemCollection.getData()) {
            SubscriptionResponseDTO subscriptionData = createSubscriptionResponse(item, subscription);
            response.add(subscriptionData);
        }

        return Optional.of(response);
    }

    @Override
    public SessionResponseDTO cancelSubscription(String email, String idempotencyKey) throws StripeException {
        Optional<CustomerPortal> customerPortal = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (customerPortal.isPresent()) {
            return new SessionResponseDTO(customerPortal.get().getSessionId(), customerPortal.get().getSessionUrl(), customerPortal.get().getCreatedAt());
        }

        User user = (User) userDetailsService.loadUserByUsername(email);
        String stripeCustomerId = user.getStripeCustomer().getStripeCustomerId();


        Subscription subscription =
                subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomerId, EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                        .orElseThrow(() -> new SubscriptionConflictException("User does not have any active subscription."));


        FlowData.SubscriptionCancel SubscriptionCancel =
                portalSessionUtil.getSubscriptionCancelConfirm(subscription.getId());

        Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomerId);
        paramsBuilder.setConfiguration(CANCEL_PORTAL_CONFIG);
        paramsBuilder.setFlowData(
                portalSessionUtil.buildPortalSessionFlow(FlowData.Type.SUBSCRIPTION_CANCEL, "http://localhost:3000/billing/cancel")
                        .setSubscriptionCancel(SubscriptionCancel)
                        .build()
        );

        RequestOptions requestOptions = portalSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        StripeSessionDTO stripeSessionDTO = new StripeSessionDTO(session.getId(), session.getUrl(), session.getObject());
        saveStripeSession(user.getStripeCustomer(), stripeSessionDTO, idempotencyKey);

        return new SessionResponseDTO(session.getId(), session.getUrl(), Instant.ofEpochSecond(session.getCreated()));
    }

    @Override
    public PaymentResponseDTO checkoutSessionSuccess(String sessionId) {
        return customerPortalRepository.findCustomerPortalById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Customer portal not found"));

    }

    private StripePrice getPriceFromPlanAvailablePrices(Set<StripePrice> stripePrices, String priceId) {
        return stripePrices.stream()
                .filter(price -> price.getStripePriceId().equals(priceId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Stripe price not found"));
    }

    private void saveStripeSession(StripeCustomer stripeCustomer, StripeSessionDTO session, String idempotencyKey) {
        CustomerPortal customerPortal = CustomerPortal.builder()
                .customer(stripeCustomer)
                .idempotencyKey(idempotencyKey)
                .sessionType(session.type())
                .sessionId(session.sessionId())
                .sessionUrl(session.sessionUrl())
                .build();
        customerPortalRepository.save(customerPortal);
    }

    private SubscriptionResponseDTO createSubscriptionResponse(SubscriptionItem item, Subscription subscription) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneOffset.UTC);
        String subscriptionPeriodEnd = formatter.format(Instant.ofEpochSecond(subscriptionUtil.getSubscriptionItemPeriodEnd(subscription)));
        String subscriptionStartDate = formatter.format(Instant.ofEpochSecond(subscription.getStartDate()));
        Product product = item.getPrice().getProductObject();
        String interval = item.getPrice().getRecurring().getInterval();
        BigDecimal amount = item.getPrice().getUnitAmountDecimal();
        String currency = item.getPrice().getCurrency();

        StripePaymentMethod stripePaymentMethod = paymentMethodRepository.findPaymentMethodByStripePaymentMethodId(subscription.getDefaultPaymentMethod())
                .orElseThrow(() -> new ResourceNotFoundException("Stripe payment method not found"));

        PaymentMethodDTO paymentMethodDTO =
                new PaymentMethodDTO(stripePaymentMethod.getLast4(),  stripePaymentMethod.getBrand(), stripePaymentMethod.getType());

        return new SubscriptionResponseDTO(
                subscription.getId(),
                product.getName(),
                interval,
                subscriptionStartDate,
                subscriptionPeriodEnd,
                amount,
                currency,
                paymentMethodDTO
        );
    }
}



