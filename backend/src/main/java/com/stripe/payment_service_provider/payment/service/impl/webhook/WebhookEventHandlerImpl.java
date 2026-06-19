package com.stripe.payment_service_provider.payment.service.impl.webhook;

import com.stripe.model.Product;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.service.StripePaymentMethodService;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.payment.repository.StripeCustomerRepository;
import com.stripe.payment_service_provider.subscription.service.PlanService;
import com.stripe.payment_service_provider.payment.service.StripeWebhookHandler;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceEventHandler;
import com.stripe.payment_service_provider.settings.exceptions.stripe.CustomerNotFoundException;
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
    private final StripeCustomerRepository stripeCustomerRepository;
    private final SubscriptionEventHandlerImpl subscriptionEventHandlerImpl;
    private final StripeInvoiceEventHandler stripeInvoiceEventHandler;
    private final SubscriptionEmailService subscriptionEmailService;
    private final StripePaymentMethodService stripePaymentMethodService;
    private final PlanService planService;

    @Override
    public void handleStripeEvent(Event event) throws StripeException {
        System.out.println(event);
        switch (event.getType()) {
            case "customer.subscription.created", "customer.subscription.updated": {
                Subscription subscription = handleSubscriptionEvent(event);
                subscriptionEventHandlerImpl.handleSubscriptionChange(subscription);
                break;
            }

            case "customer.subscription.deleted": {
                Subscription subscription = handleSubscriptionEvent(event);
                UserSubscription deletedUserSubscription = subscriptionEventHandlerImpl.handleSubscriptionCancellation(subscription);
                subscriptionEmailService.sendSubscriptionCancelledEmail(deletedUserSubscription.getStripeCustomer().getEmail(), deletedUserSubscription);
                break;
            }

            case "customer.deleted": {
                Customer customer = handleCustomerEvent(event);
                StripeCustomer stripeCustomer = stripeCustomerRepository.findStripeCustomerByStripeCustomerId(customer.getId())
                        .orElseThrow(() -> new CustomerNotFoundException("No customer found with id: " + customer.getId()));
                stripeCustomerRepository.delete(stripeCustomer);
                break;
            }

            case "invoice.payment_succeeded", "invoice.payment_failed": {
                Invoice invoice = handleInvoiceEvent(event);
                stripeInvoiceEventHandler.handleInvoicePaymentUpdate(invoice);
                break;
            }

            case "invoice.upcoming": {
                Invoice invoice = handleInvoiceEvent(event);
                stripeInvoiceEventHandler.handleUpcomingInvoice(invoice);
                break;
            }

            case "product.created", "product.updated": {
                Product product = handleProductEvent(event);
                System.out.println(product);
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
                System.out.println(price);
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
