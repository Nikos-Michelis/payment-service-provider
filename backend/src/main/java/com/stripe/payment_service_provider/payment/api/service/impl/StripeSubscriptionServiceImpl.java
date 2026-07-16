package com.stripe.payment_service_provider.payment.api.service.impl;

import com.stripe.model.Product;
import com.stripe.payment_service_provider.payment.api.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.api.dto.payment.response.PaymentResponseDTO;
import com.stripe.payment_service_provider.payment.api.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.api.dto.payment.response.SubscriptionResponseDTO;
import com.stripe.payment_service_provider.payment.api.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.api.model.StripePaymentMethod;
import com.stripe.payment_service_provider.payment.api.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.api.repository.CustomerRepository;
import com.stripe.payment_service_provider.payment.api.service.StripeSubscriptionService;
import com.stripe.payment_service_provider.payment.api.util.*;
import com.stripe.payment_service_provider.settings.exceptions.common.ConflictException;
import com.stripe.payment_service_provider.subscription.model.StripePrice;
import com.stripe.payment_service_provider.subscription.model.SubscriptionStatus;
import com.stripe.payment_service_provider.payment.api.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.settings.exceptions.auth.SessionNotFoundException;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.api.dto.payment.request.SubscriptionRequestDTO;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
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
public class StripeSubscriptionServiceImpl implements StripeSubscriptionService {
    private final SubscriptionUtil subscriptionUtil;
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final PortalSessionUtil portalSessionUtil;
    private final CustomerUtil customerUtil;
    private final ProductUtil productUtil;
    private final PlanRepository planRepository;
    private final CustomerPortalRepository customerPortalRepository;
    private final CustomerRepository customerRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    @Value("${application.api.stripe.tier.trial.period}")
    private Long trialPeriod;
    @Value("${application.api.stripe.hosted.configs.update-id}")
    private String UPDATE_PORTAL_CONFIG;
    @Value("${application.api.stripe.hosted.configs.cancel-id}")
    private String CANCEL_PORTAL_CONFIG;
    @Value("${application.api.stripe.hosted.redirect.success}")
    private String FRONTEND_SUCCESS_URL;
    @Value("${application.api.stripe.hosted.redirect.cancel}")
    private String FRONTEND_CANCEL_URL;

    @Override
    public String renewSubscription(String email, String idempotencyKey) throws StripeException {
        Customer stripeCustomer = customerUtil.findCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Subscription subscription = subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomer.getId(), EnumSet.of(SubscriptionStatus.PAST_DUE, SubscriptionStatus.UNPAID))
                .orElseThrow(() -> new ResourceNotFoundException("User " + stripeCustomer.getId() + " does not have any expired subscription."));

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Invoice latestInvoice = Invoice.retrieve(subscription.getLatestInvoice(), requestOptions);

        return latestInvoice.getHostedInvoiceUrl();
    }

    @Override
    @Transactional
    public SessionResponseDTO createSubscription(SubscriptionRequestDTO paymentRequest, String email, String idempotencyKey) throws StripeException {
        Optional<CustomerPortal> customerPortal = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (customerPortal.isPresent()) {
            return new SessionResponseDTO(
                    customerPortal.get().getSessionId(),
                    customerPortal.get().getSessionUrl(),
                    customerPortal.get().getSessionType(),
                    customerPortal.get().getCreatedAt()
            );
        }

        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(email);
        String stripeCustomerId = stripeCustomer.getStripeCustomerId();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(paymentRequest.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found any plan with the provided id " +  paymentRequest.productId()));

        StripePrice stripePrice = productUtil.getPriceFromPlanAvailablePrices(stripePlan.getPrices(), paymentRequest.priceId());

        Optional<Subscription> subscription = subscriptionUtil.getSubscriptionByStatusAndCustomer(
                stripeCustomer.getStripeCustomerId(), EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING));

        if (subscription.isPresent()) {
            throw new ConflictException("User already has an active subscription.");
        }

        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(SessionCreateParams.Mode.SUBSCRIPTION, stripeCustomerId);
        Product product = productUtil.buildStripePlan(stripePlan, stripePrice);

        SessionCreateParams.LineItem lineItem = subscriptionUtil.buildSubscriptionLineItem(product);
        paramsBuilder.addLineItem(lineItem);
        SessionCreateParams sessionCreateParams = paramsBuilder.build();

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Session session = checkoutSessionUtil.createCheckoutSession(sessionCreateParams, requestOptions);

        SessionResponseDTO sessionResponseDTO = new SessionResponseDTO(session.getId(), session.getUrl(), session.getObject(), Instant.ofEpochSecond(session.getCreated()));
        saveSession(stripeCustomer, sessionResponseDTO, idempotencyKey);

        return sessionResponseDTO;
    }

    @Override
    public SessionResponseDTO updateSubscription(SubscriptionRequestDTO paymentRequest, String email, String idempotencyKey) throws StripeException {
        Optional<CustomerPortal> customerPortal = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (customerPortal.isPresent()) {
            return new SessionResponseDTO(
                    customerPortal.get().getSessionId(),
                    customerPortal.get().getSessionUrl(),
                    customerPortal.get().getSessionType(),
                    customerPortal.get().getCreatedAt()
            );
        }

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(paymentRequest.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found any plan with the provided id " +  paymentRequest.productId()));

        Subscription subscription =
                subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomer.getStripeCustomerId(), EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                        .orElseThrow(() -> new ConflictException("User does not have any active subscription."));
        String subscriptionItemId = subscription.getItems().getData().getFirst().getId();

        StripePrice stripePrice = productUtil.getPriceFromPlanAvailablePrices(stripePlan.getPrices(), paymentRequest.productId());

        SubscriptionUpdateConfirm subscriptionUpdateConfirm =
                portalSessionUtil.getSubscriptionUpdateConfirm(subscription.getId(), subscriptionItemId, stripePrice.getStripePriceId());

        Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomer.getStripeCustomerId());
        paramsBuilder.setConfiguration(UPDATE_PORTAL_CONFIG);
        paramsBuilder.setFlowData(
                portalSessionUtil.buildPortalSessionFlow(FlowData.Type.SUBSCRIPTION_UPDATE_CONFIRM, FRONTEND_SUCCESS_URL)
                        .setSubscriptionUpdateConfirm(subscriptionUpdateConfirm)
                        .build()
        );

        RequestOptions requestOptions = portalSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        SessionResponseDTO sessionResponseDTO = new SessionResponseDTO(session.getId(), session.getUrl(), session.getObject(), Instant.ofEpochSecond(session.getCreated()));
        saveSession(stripeCustomer, sessionResponseDTO, idempotencyKey);

        return sessionResponseDTO;
    }

    @Override
    public Optional<List<SubscriptionResponseDTO>> findSubscriptionByCustomerEmail(String email) throws StripeException {

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByEmail(email)
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
            return new SessionResponseDTO(
                    customerPortal.get().getSessionId(),
                    customerPortal.get().getSessionUrl(),
                    customerPortal.get().getSessionType(),
                    customerPortal.get().getCreatedAt()
            );
        }

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not have any active subscription."));

        Subscription subscription =
                subscriptionUtil.getSubscriptionByStatusAndCustomer(stripeCustomer.getStripeCustomerId(), EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.TRIALING))
                        .orElseThrow(() -> new ConflictException("User does not have any active subscription."));

        FlowData.SubscriptionCancel SubscriptionCancel =
                portalSessionUtil.getSubscriptionCancelConfirm(subscription.getId());

        Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomer.getStripeCustomerId());
        paramsBuilder.setConfiguration(CANCEL_PORTAL_CONFIG);
        paramsBuilder.setFlowData(
                portalSessionUtil.buildPortalSessionFlow(FlowData.Type.SUBSCRIPTION_CANCEL, FRONTEND_CANCEL_URL)
                        .setSubscriptionCancel(SubscriptionCancel)
                        .build()
        );

        RequestOptions requestOptions = portalSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        SessionResponseDTO sessionResponseDTO = new SessionResponseDTO(session.getId(), session.getUrl(), session.getObject(), Instant.ofEpochSecond(session.getCreated()));
        saveSession(stripeCustomer, sessionResponseDTO, idempotencyKey);

        return sessionResponseDTO;
    }

    @Override
    public PaymentResponseDTO checkoutSessionSuccess(String sessionId) {
        return customerPortalRepository.findCustomerPortalById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Customer portal not found"));

    }

    private void saveSession(StripeCustomer stripeCustomer, SessionResponseDTO session, String idempotencyKey) {
        CustomerPortal customerPortal = CustomerPortal.builder()
                .customer(stripeCustomer)
                .idempotencyKey(idempotencyKey)
                .sessionType(session.sessionType())
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

        PaymentMethodDTO paymentMethodDTO = new PaymentMethodDTO(stripePaymentMethod.getLast4(),  stripePaymentMethod.getBrand(), stripePaymentMethod.getType());

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



