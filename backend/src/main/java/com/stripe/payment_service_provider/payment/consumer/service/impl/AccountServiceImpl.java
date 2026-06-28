package com.stripe.payment_service_provider.payment.consumer.service.impl;

import com.stripe.payment_service_provider.payment.consumer.dto.PaymentMethodDTO;
import com.stripe.payment_service_provider.payment.consumer.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.consumer.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.consumer.model.StripePaymentMethod;
import com.stripe.payment_service_provider.payment.consumer.service.StripeAccountService;
import com.stripe.payment_service_provider.payment.consumer.util.CustomerUtil;
import com.stripe.payment_service_provider.payment.consumer.util.PortalSessionUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.net.RequestOptions;
import com.stripe.param.billingportal.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements StripeAccountService {
    private final CustomerUtil customerUtil;
    private final PortalSessionUtil portalSessionUtil;
    @Value("${application.api.stripe.hosted.configs.account-management-id}")
    private String ACCOUNT_PORTAL_CONFIG;

    @Override
    public SessionResponseDTO getStripeAccountSettings(String email, String idempotencyKey) throws StripeException {
        Customer stripeCustomer = customerUtil.findCustomerByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer does not found with email: " + email));

        String stripeCustomerId = stripeCustomer.getId();

        SessionCreateParams.Builder paramsBuilder = portalSessionUtil.buildPortalSessionParams(stripeCustomerId);
        paramsBuilder.setConfiguration(ACCOUNT_PORTAL_CONFIG);

        RequestOptions requestOptions = portalSessionUtil.getIdempotencyKey(idempotencyKey);
        com.stripe.model.billingportal.Session session = portalSessionUtil.createPortalSession(paramsBuilder.build(), requestOptions);
        return new SessionResponseDTO(session.getId(), session.getUrl(), session.getObject(), Instant.ofEpochMilli(session.getCreated()));
    }

    @Override
    public PaymentMethodDTO getDefaultPaymentMethod(StripeCustomer stripeCustomer) {
        if (stripeCustomer == null) {
            throw new ResourceNotFoundException("Customer does not exist");
        }

        if (stripeCustomer.getStripePaymentMethods() == null) {
            throw new ResourceNotFoundException("Stripe payment methods not found");
        }

        StripePaymentMethod stripePaymentMethod = stripeCustomer.getStripePaymentMethods().stream().filter(StripePaymentMethod::getIsDefault).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Customer does not have any default payment method"));

        return new PaymentMethodDTO(stripePaymentMethod.getLast4(), stripePaymentMethod.getBrand(), stripePaymentMethod.getType());
    }
}
