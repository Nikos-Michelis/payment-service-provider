package com.stripe.payment_service_provider.payment.service.impl.webhook;

import com.stripe.model.*;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.payment_service_provider.payment.dto.email.SubscriptionEmailContext;
import com.stripe.payment_service_provider.payment.model.*;
import com.stripe.payment_service_provider.payment.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.repository.PaymentRepository;
import com.stripe.payment_service_provider.payment.util.InvoiceUtil;
import com.stripe.payment_service_provider.payment.util.PriceUtil;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.repository.CustomerRepository;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceEventHandler;
import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import com.stripe.payment_service_provider.user.reporitory.UserRepository;
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
    private final UserRepository userRepository;
    private final InvoiceUtil invoiceUtil;
    private final PriceUtil priceUtil;

    @Override
    public void handleUpcomingInvoice(Invoice invoice) {
        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(stripeCustomer.getSubscriptions())
                .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));

        SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getPrice().getBilingCycle())
                .accessStartDate(userSubscription.getCurrentPeriodEnd())
                .build();

        subscriptionEmailService.sendSubscriptionExpirationNotification(subscriptionEmailContext);
    }

    @Transactional
    @Override
    public void handleInvoicePaymentUpdate(Invoice invoice) {
        String billingReason = invoice.getBillingReason();
        String customerId = invoice.getCustomer();

        Invoice retrivedInvoice = invoiceUtil.getInvoiceById(invoice.getId());

        PaymentIntent paymentIntent = invoiceUtil.getPaymentIntentObjectFromInvoice(retrivedInvoice);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        StripePaymentMethod stripePaymentMethod = paymentMethodRepository.findPaymentMethodByStripePaymentMethodId(paymentIntent.getPaymentMethod())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found " +  paymentIntent.getPaymentMethod()));

        switch (billingReason) {
            case "manual": {
               /* subscriptionEmailService.sendSubscriptionSuccessEmail(
                        stripeCustomer.getEmail(),
                        userSubscription,
                        invoice
                );*/
                break;
            }

            case "subscription_create": {
                String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

                UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

                StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, billingReason);
                StripePayment stripePayment = buildPayment(paymentIntent, stripeInvoice, stripeCustomer, stripePaymentMethod);
                paymentRepository.save(stripePayment);

                SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                        .email(stripeCustomer.getEmail())
                        .planName(userSubscription.getStripePlan().getName())
                        .billingCycle(userSubscription.getPrice().getBilingCycle())
                        .amount(invoice.getAmountPaid())
                        .currency(invoice.getCurrency())
                        .invoicePdf(invoice.getInvoicePdf())
                        .accessStartDate(userSubscription.getCurrentPeriodEnd())
                        .build();

                subscriptionEmailService.sendSubscriptionSuccessEmail(subscriptionEmailContext);
                break;
            }

            case "subscription_update": {
                String productId = invoice.getLines().getData().getFirst().getPricing().getPriceDetails().getProduct();
                String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

                UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

                StripePlan stripePlan = planRepository.findSubscriptionPlanByStripeProductId(productId)
                        .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

                StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, billingReason);
                StripePayment stripePayment = buildPayment(paymentIntent, stripeInvoice, stripeCustomer, stripePaymentMethod);

                paymentRepository.save(stripePayment);

                SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                        .email(stripeCustomer.getEmail())
                        .planName(stripePlan.getName())
                        .previousPlanName(userSubscription.getStripePlan().getName())
                        .billingCycle(userSubscription.getPrice().getBilingCycle())
                        .accessStartDate(invoice.getEffectiveAt())
                        .build();

                subscriptionEmailService.sendSubscriptionUpdateEmail(subscriptionEmailContext);
                break;
            }

            case "subscription_cycle": {

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
                        invoice
                );*/
                break;
            }

            default: {
                log.warn("Unhandled billing reason: {}", billingReason);
                break;
            }

        }
    }


    public void handleNewSubscriptionInvoice(Invoice invoice) {
        PaymentIntent paymentIntent = invoiceUtil.getPaymentIntentObjectFromInvoice(invoice);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + invoice.getCustomer()));

        StripePaymentMethod stripePaymentMethod = paymentMethodRepository.findPaymentMethodByStripePaymentMethodId(paymentIntent.getPaymentMethod())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found " +  paymentIntent.getPaymentMethod()));

        String subscriptionId = invoice.getParent().getSubscriptionDetails().getSubscription();

        UserSubscription userSubscription = subscriptionService.getSubscriptionBySubscriptionId(subscriptionId);

        StripeInvoice stripeInvoice = buildStripeInvoice(invoice, userSubscription, invoice.getBillingReason());
        StripePayment stripePayment = buildPayment(paymentIntent, stripeInvoice, stripeCustomer, stripePaymentMethod);
        paymentRepository.save(stripePayment);

        SubscriptionEmailContext subscriptionEmailContext = SubscriptionEmailContext.builder()
                .email(stripeCustomer.getEmail())
                .planName(userSubscription.getStripePlan().getName())
                .billingCycle(userSubscription.getPrice().getBilingCycle())
                .amount(invoice.getAmountPaid())
                .currency(invoice.getCurrency())
                .invoicePdf(invoice.getInvoicePdf())
                .accessStartDate(userSubscription.getCurrentPeriodEnd())
                .build();

        subscriptionEmailService.sendSubscriptionSuccessEmail(subscriptionEmailContext);

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

    private StripePayment buildPayment(PaymentIntent paymentIntent, StripeInvoice stripeInvoice, StripeCustomer stripeCustomer, StripePaymentMethod stripePaymentMethod) {
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
