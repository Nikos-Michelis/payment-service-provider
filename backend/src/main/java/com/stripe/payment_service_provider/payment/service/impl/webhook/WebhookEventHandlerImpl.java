package com.stripe.payment_service_provider.payment.service.impl.webhook;

import com.stripe.model.Product;
import com.stripe.payment_service_provider.payment.dto.email.SubscriptionEmailContext;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.service.StripePaymentMethodService;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.repository.CustomerRepository;
import com.stripe.payment_service_provider.subscription.service.PlanService;
import com.stripe.payment_service_provider.payment.service.StripeWebhookHandler;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceEventHandler;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookEventHandlerImpl implements StripeWebhookHandler {
    private final CustomerRepository customerRepository;
    private final SubscriptionEventHandlerImpl subscriptionEventHandlerImpl;
    private final StripeInvoiceEventHandler stripeInvoiceEventHandler;
    private final SubscriptionEmailService subscriptionEmailService;
    private final StripePaymentMethodService stripePaymentMethodService;
    private final PlanService planService;

    @Override
    public void handleStripeEvent(Event event) throws StripeException {
        switch (event.getType()) {
            case "customer.subscription.created": {
                Subscription subscription = handleSubscriptionEvent(event);
                subscriptionEventHandlerImpl.handleSubscriptionCreate(subscription);
                break;
            }

            case "customer.subscription.updated": {
                Subscription subscription = handleSubscriptionEvent(event);
                subscriptionEventHandlerImpl.handleSubscriptionUpdate(subscription);
                break;
            }

            case "customer.subscription.deleted": {
                Subscription subscription = handleSubscriptionEvent(event);
                UserSubscription deletedUserSubscription = subscriptionEventHandlerImpl.handleSubscriptionCancel(subscription);

                SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                        .email(deletedUserSubscription.getStripeCustomer().getEmail())
                        .planName(deletedUserSubscription.getStripePlan().getName())
                        .accessEndDate(deletedUserSubscription.getCurrentPeriodEnd())
                        .build();

                subscriptionEmailService.sendSubscriptionCancelledEmail(subscriptionEmailContext);
                break;
            }

            case "customer.deleted": {
                Customer customer = handleCustomerEvent(event);
                StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(customer.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + customer.getId()));
                customerRepository.delete(stripeCustomer);
                break;
            }

            case "invoice.payment_succeeded", "invoice.payment_failed": {
                Invoice invoice = handleInvoiceEvent(event);
                stripeInvoiceEventHandler.handleInvoicePayment(invoice);
                break;
            }

            case "invoice.upcoming": {
                Invoice invoice = handleInvoiceEvent(event);
                stripeInvoiceEventHandler.handleUpcomingInvoice(invoice);
                break;
            }

            case "product.created", "product.updated": {
                Product product = handleProductEvent(event);
                planService.createOrUpdatePlan(product);
                break;
            }

            case "product.deleted": {
                Product product = handleProductEvent(event);
                planService.deletePlan(product);
                break;
            }

            case "price.created", "price.updated": {
                Price price = handlePriceEvent(event);
                planService.createOrUpdatePrice(price);
                break;
            }

            case "payment_method.attached", "payment_method.updated": {
                PaymentMethod paymentMethod = handlePaymentMethodEvent(event);
                stripePaymentMethodService.addPaymentMethod(paymentMethod);
                break;
            }

            case "payment_method.detached": {
                PaymentMethod paymentMethod = handlePaymentMethodEvent(event);
                stripePaymentMethodService.removePaymentMethod(paymentMethod);
                break;
            }

            default: {
                log.warn("Unhandled event type: {}", event.getType());
                break;
            }
        }
    }

    private PaymentMethod handlePaymentMethodEvent(Event event) {
        return (PaymentMethod) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found"));
    }

    private Price handlePriceEvent(Event event) {
        return (Price) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Price not found"));
    }

    private Product handleProductEvent(Event event) {
        return (Product) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Subscription handleSubscriptionEvent(Event event) {
        return (Subscription) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
    }

    private Customer handleCustomerEvent(Event event) {
        return (Customer) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private Invoice handleInvoiceEvent(Event event) {
        return (Invoice) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }
}
