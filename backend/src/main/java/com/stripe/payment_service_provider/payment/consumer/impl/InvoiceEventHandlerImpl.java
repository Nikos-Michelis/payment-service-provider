package com.stripe.payment_service_provider.payment.consumer.impl;

import com.stripe.model.*;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.payment_service_provider.payment.api.dto.email.SubscriptionEmailContext;
import com.stripe.payment_service_provider.payment.consumer.InvoiceEventHandler;
import com.stripe.payment_service_provider.payment.api.model.*;
import com.stripe.payment_service_provider.payment.api.repository.InvoiceRepository;
import com.stripe.payment_service_provider.payment.api.repository.PaymentRepository;
import com.stripe.payment_service_provider.payment.api.util.InvoiceUtil;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.api.repository.CustomerRepository;
import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceEventHandlerImpl implements InvoiceEventHandler {
    private final SubscriptionEmailService subscriptionEmailService;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserSubscriptionServiceImpl subscriptionService;
    private final PlanRepository planRepository;
    private final InvoiceUtil invoiceUtil;

    @Override
    public void onInvoiceUpcoming(Event event) {
        Invoice invoice = handleInvoiceEvent(event);
        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(stripeCustomer.getSubscriptions())
                .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));

        SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getStripePrice().getBillingCycle())
                .accessStartDate(userSubscription.getCurrentPeriodEnd())
                .build();

        subscriptionEmailService.sendSubscriptionExpirationNotification(subscriptionEmailContext);
    }

    @Transactional
    @Override
    public void onInvoicePaid(Event event) {
        Invoice invoice = handleInvoiceEvent(event);
        String billingReason = invoice.getBillingReason();
        Invoice retrivedInvoice = invoiceUtil.getInvoiceById(invoice.getId());

        switch (billingReason) {
            case "subscription_create" -> invoiceOnSubscriptionCreate(retrivedInvoice);
            case "subscription_update" -> invoiceOnSubscriptionUpdate(retrivedInvoice);
            case "subscription_cycle" -> invoiceOnSubscriptionCycle(retrivedInvoice);
            default -> log.warn("Unhandled billing reason: {}", billingReason);
        }
    }

    @Transactional
    @Override
    public void onInvoiceUpdate(Event event) {
        Invoice invoice = handleInvoiceEvent(event);
        StripeInvoice stripeInvoice = invoiceRepository.findStripeInvoiceByInvoiceId(invoice.getId())
                .orElseThrow(() ->  new ResourceNotFoundException("Invoice not found with id: " + invoice.getId()));

        stripeInvoice.setStatus(InvoiceStatus.valueOf(stripeInvoice.getStatus().getName().toUpperCase()));
        invoiceRepository.save(stripeInvoice);
    }

    public void invoiceOnSubscriptionCreate(Invoice invoice) {
        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
        UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, invoice.getBillingReason());
        invoiceRepository.save(stripeInvoice);

        SubscriptionEmailContext subscriptionEmailContext =  SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getStripePrice().getBillingCycle())
                .amount(invoice.getAmountPaid())
                .currency(invoice.getCurrency())
                .invoicePdf(invoice.getInvoicePdf())
                .accessStartDate(userSubscription.getCurrentPeriodEnd())
                .build();

        subscriptionEmailService.sendSubscriptionSuccessEmail(subscriptionEmailContext);
    }

    public void invoiceOnSubscriptionUpdate(Invoice invoice) {
        String productId = invoice.getLines().getData().getFirst().getPricing().getPriceDetails().getProduct();
        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, invoice.getBillingReason());
        invoiceRepository.save(stripeInvoice);

        SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(stripePlan.getName())
                .previousPlanName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getStripePrice().getBillingCycle())
                .accessStartDate(Instant.ofEpochSecond(invoice.getEffectiveAt()))
                .build();

        subscriptionEmailService.sendSubscriptionUpdateEmail(subscriptionEmailContext);
    }

    public void invoiceOnSubscriptionCycle(Invoice invoice) {
        String productId = invoice.getLines().getData().getFirst().getPricing().getPriceDetails().getProduct();
        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, invoice.getBillingReason());
        invoiceRepository.save(stripeInvoice);

        SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(stripePlan.getName())
                .previousPlanName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getStripePrice().getBillingCycle())
                .accessStartDate(Instant.ofEpochSecond(invoice.getEffectiveAt()))
                .build();

        subscriptionEmailService.sendSubscriptionRenewalEmail(subscriptionEmailContext);
    }

    @Override
    public void onInvoicePaymentPaid(Event event) {
        InvoicePayment invoicePayment = handleInvoicePaymentEvent(event);
        StripePayment stripePayment = paymentRepository.findStripePaymentByPaymentIntentId(invoicePayment.getPayment().getPaymentIntent())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + invoicePayment.getPayment().getPaymentIntent()));

        stripePayment.setInvoiceId(invoicePayment.getInvoice());
        paymentRepository.save(stripePayment);
    }

    private StripeInvoice buildStripeInvoice(Invoice invoice, UserSubscription userSubscription, String billingReason) {
        return StripeInvoice.builder()
                .invoiceId(invoice.getId())
                .subscription(userSubscription)
                .amountPaid(BigDecimal.valueOf(invoice.getAmountPaid()))
                .currency(invoice.getCurrency().toUpperCase())
                .status(InvoiceStatus.valueOf(invoice.getStatus().toUpperCase()))
                .billingReason(BillingReason.valueOf(billingReason.toUpperCase()))
                .hostedInvoiceUrl(invoice.getHostedInvoiceUrl())
                .invoiceCreatedAt(Instant.ofEpochSecond(invoice.getCreated()))
                .finalizedAt(Instant.ofEpochSecond(invoice.getStatusTransitions().getFinalizedAt()))
                .build();
    }

    private Invoice handleInvoiceEvent(Event event) {
        return (Invoice) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    private InvoicePayment handleInvoicePaymentEvent(Event event) {
        return (InvoicePayment) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("InvoicePayment not found"));
    }
}
