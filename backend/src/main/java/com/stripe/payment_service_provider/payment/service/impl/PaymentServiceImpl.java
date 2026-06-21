package com.stripe.payment_service_provider.payment.service.impl;

import com.stripe.payment_service_provider.payment.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.payment.util.CheckoutSessionUtil;
import com.stripe.payment_service_provider.payment.util.CustomerUtil;
import com.stripe.payment_service_provider.payment.util.ProductUtil;
import com.stripe.payment_service_provider.payment.util.SubscriptionUtil;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {
    private final SubscriptionUtil subscriptionUtil;
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final CustomerUtil customerUtil;
    private final ProductUtil productUtil;
    private final PlanRepository planRepository;
    private final ProductRepository productRepository;
    private final CustomerPortalRepository customerPortalRepository;

    /*@Transactional
    public String createOneTimePayment(
            PaymentRequestDTO paymentRequest,
            String idempotencyKey
    ) throws StripeException {

        Optional<CustomerPortal> existingSession = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (existingSession.isPresent()) {
            return existingSession.get().getSessionUrl();
        }

        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(paymentRequest.email());

        SessionCreateParams.Builder paramsBuilder =
                checkoutSessionUtil.buildCheckoutSession(
                        SessionCreateParams.Mode.PAYMENT,
                        stripeCustomer.getStripeCustomerId()
                );
        // should create a line item for each product in order
        *//*for (Product product : paymentRequest.products()) {
            Product dbProduct = productUtil.buildStripeProduct(product);
            SessionCreateParams.LineItem lineItem = checkoutSessionUtil.buildOneTimePaymentLineItem(dbProduct);
            paramsBuilder.addLineItem(lineItem);
        }*//*

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Session session = Session.create(paramsBuilder.build(), requestOptions);

        StripeSession stripeSession = new StripeSession(session.getId(), session.getUrl());
        saveStripeSession(stripeCustomer, stripeSession, idempotencyKey);

        return session.getUrl();
    }*/
}
