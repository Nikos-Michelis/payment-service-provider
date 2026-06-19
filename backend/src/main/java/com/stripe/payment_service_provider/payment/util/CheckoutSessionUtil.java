package com.stripe.payment_service_provider.payment.util;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.payment_service_provider.settings.exceptions.stripe.StripeSessionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.stripe.param.checkout.SessionCreateParams.SavedPaymentMethodOptions.AllowRedisplayFilter;

@Component
public class CheckoutSessionUtil {
    @Value("${application.frontend.url}")
    private String clientBaseURL;


    public Session createCheckoutSession(SessionCreateParams sessionCreateParams, RequestOptions requestOptions) throws StripeSessionException {
        try {
            return Session.create(sessionCreateParams, requestOptions);
        } catch (StripeException e) {
            throw new StripeSessionException("Oops! Something went wrong", "Failed to create checkout portal session." + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public SessionCreateParams.Builder buildCheckoutSession(SessionCreateParams.Mode mode, String customerId) {
        return SessionCreateParams.builder()
                .setMode(mode)
                .setCustomer(customerId)
                .setSavedPaymentMethodOptions(getPaymentMethodOptions())
                .setAutomaticTax(getCustomerTax())
                .setCustomerUpdate(getCustomerAddress())
                .setSuccessUrl(clientBaseURL + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(clientBaseURL + "/failure");
    }

    public RequestOptions getIdempotencyKey(String idempotencyKey) {
        return RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
    }

    private SessionCreateParams.SavedPaymentMethodOptions getPaymentMethodOptions() {
        return SessionCreateParams.SavedPaymentMethodOptions.builder().addAllowRedisplayFilter(AllowRedisplayFilter.ALWAYS).build();
    }

    private SessionCreateParams.AutomaticTax getCustomerTax() {
        return SessionCreateParams.AutomaticTax.builder().setEnabled(true).build();
    }

    private SessionCreateParams.CustomerUpdate getCustomerAddress() {
        return SessionCreateParams.CustomerUpdate.builder()
                .setAddress(SessionCreateParams.CustomerUpdate.Address.AUTO)
                .setName(SessionCreateParams.CustomerUpdate.Name.AUTO)
                .build();
    }
}
