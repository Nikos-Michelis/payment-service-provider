package com.stripe.payment_service_provider.payment.util;

import com.stripe.payment_service_provider.settings.exceptions.stripe.StripeSessionException;
import com.stripe.exception.StripeException;
import com.stripe.model.billingportal.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.billingportal.SessionCreateParams;
import com.stripe.param.billingportal.SessionCreateParams.FlowData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
@Component
public class PortalSessionUtil {

    @Value("${application.frontend.url}")
    private String clientBaseURL;

    public Session createPortalSession(SessionCreateParams sessionCreateParams, RequestOptions requestOptions) throws StripeSessionException {
        try {
            return Session.create(sessionCreateParams, requestOptions);
        } catch (StripeException e) {
            throw new StripeSessionException("Oops! Something went wrong", "Failed to create billing portal session." + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    public RequestOptions getIdempotencyKey(String idempotencyKey) {
        return RequestOptions.builder().setIdempotencyKey(idempotencyKey).build();
    }

    public SessionCreateParams.Builder buildPortalSessionParams(String customerId) {
        return SessionCreateParams.builder()
                .setCustomer(customerId)
                .setReturnUrl(clientBaseURL + "/settings");
    }

    public FlowData.Builder buildPortalSessionFlow(FlowData.Type type, String redirectUrl) {
        return FlowData.builder()
                .setType(type)
                .setAfterCompletion(getAfterCompletionFlow(redirectUrl));
    }

    public FlowData.SubscriptionCancel getSubscriptionCancelConfirm(String subscriptionId) {
        return FlowData.SubscriptionCancel.builder()
                .setSubscription(subscriptionId)
                .build();
    }

    public FlowData.SubscriptionUpdateConfirm getSubscriptionUpdateConfirm(String subscriptionId, String subscriptionItemId, String priceId) {
        return FlowData.SubscriptionUpdateConfirm.builder()
                .setSubscription(subscriptionId)
                .addItem(FlowData.SubscriptionUpdateConfirm.Item
                        .builder()
                        .setId(subscriptionItemId)
                        .setPrice(priceId)
                        .build())
                .build();
    }

    private FlowData.AfterCompletion getAfterCompletionFlow(String returnUrl) {
        return FlowData.AfterCompletion.builder()
                .setType(FlowData.AfterCompletion.Type.REDIRECT)
                .setRedirect(
                        FlowData.AfterCompletion.Redirect
                                .builder()
                                .setReturnUrl(returnUrl)
                                .build()
                )
                .build();
    }
}

