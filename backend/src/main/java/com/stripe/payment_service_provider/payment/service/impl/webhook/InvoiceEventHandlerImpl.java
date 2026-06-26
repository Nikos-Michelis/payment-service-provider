package com.stripe.payment_service_provider.payment.service.impl.webhook;

import com.stripe.model.*;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.payment_service_provider.payment.dto.email.SubscriptionEmailContext;
import com.stripe.payment_service_provider.payment.model.*;
import com.stripe.payment_service_provider.payment.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.repository.PaymentRepository;
import com.stripe.payment_service_provider.payment.util.InvoiceUtil;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.repository.CustomerRepository;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceEventHandler;
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
public class InvoiceEventHandlerImpl implements StripeInvoiceEventHandler {
    private final SubscriptionEmailService subscriptionEmailService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final UserSubscriptionServiceImpl subscriptionService;
    private final PlanRepository planRepository;
    private final InvoiceUtil invoiceUtil;

    @Override
    public void handleUpcomingInvoice(Invoice invoice) {
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
    public void handleInvoicePayment(Invoice invoice) {
        String billingReason = invoice.getBillingReason();
        Invoice retrivedInvoice = invoiceUtil.getInvoiceById(invoice.getId());

        switch (billingReason) {
            case "manual" -> System.out.println("manual");
            case "subscription_create" -> handleNewSubscriptionInvoice(retrivedInvoice);
            case "subscription_update" -> handleUpdateSubscriptionInvoice(retrivedInvoice);
            case "subscription_cycle" -> handleSubscriptionCycle(retrivedInvoice);
            default -> log.warn("Unhandled billing reason: {}", billingReason);
        }
    }

    public void handleUpdateSubscriptionInvoice(Invoice invoice) {
        String productId = invoice.getLines().getData().getFirst().getPricing().getPriceDetails().getProduct();
        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        PaymentIntent paymentIntent = invoiceUtil.getPaymentIntentObjectFromInvoice(invoice);

        StripePaymentMethod stripePaymentMethod = paymentMethodRepository.findPaymentMethodByStripePaymentMethodId(paymentIntent.getPaymentMethod())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found " +  paymentIntent.getPaymentMethod()));

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, invoice.getBillingReason());
        StripePayment stripePayment = buildPayment(paymentIntent, stripeInvoice, stripeCustomer, stripePaymentMethod);
        paymentRepository.save(stripePayment);

        SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(stripePlan.getName())
                .previousPlanName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getStripePrice().getBillingCycle())
                .accessStartDate(Instant.ofEpochSecond(invoice.getEffectiveAt()))
                .build();

        subscriptionEmailService.sendSubscriptionUpdateEmail(subscriptionEmailContext);
    }

    public void handleNewSubscriptionInvoice(Invoice invoice) {
        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();
        UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        PaymentIntent paymentIntent = invoiceUtil.getPaymentIntentObjectFromInvoice(invoice);

        StripePaymentMethod stripePaymentMethod = paymentMethodRepository.findPaymentMethodByStripePaymentMethodId(paymentIntent.getPaymentMethod())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found " +  paymentIntent.getPaymentMethod()));

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, invoice.getBillingReason());
        StripePayment stripePayment = buildPayment(paymentIntent, stripeInvoice, stripeCustomer, stripePaymentMethod);

        paymentRepository.save(stripePayment);

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

    public void handleSubscriptionCycle(Invoice invoice) {
        /*SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(stripePlan.getName())
                .previousPlanName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getPrice().getBilingCycle())
                .accessStartDate(invoice.getEffectiveAt())
                .build();

        subscriptionEmailService.sendSubscriptionRenewalEmail(
                customerEmail,
                userSubscription,
                invoice);*/
    }

    private StripeInvoice buildStripeInvoice(Invoice invoice, UserSubscription userSubscription, String billingReason) {
        return StripeInvoice.builder()
                .invoiceStripeId(invoice.getId())
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

    private StripePayment buildPayment(
            PaymentIntent paymentIntent,
            StripeInvoice stripeInvoice,
            StripeCustomer stripeCustomer,
            StripePaymentMethod stripePaymentMethod) {

        return StripePayment.builder()
                .customer(stripeCustomer)
                .paymentIntentId(paymentIntent.getId())
                .invoice(stripeInvoice)
                .paymentMethod(stripePaymentMethod)
                .amountPaid(BigDecimal.valueOf(paymentIntent.getAmount()))
                .currency(paymentIntent.getCurrency().toUpperCase())
                .status(PaymentStatus.valueOf(paymentIntent.getStatus().toUpperCase()))
                .build();
    }
}
