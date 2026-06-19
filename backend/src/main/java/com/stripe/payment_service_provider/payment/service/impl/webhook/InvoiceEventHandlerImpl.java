package com.stripe.payment_service_provider.payment.service.impl.webhook;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.InvoiceRetrieveParams;
import com.stripe.payment_service_provider.email.service.SubscriptionEmailService;
import com.stripe.payment_service_provider.payment.model.*;
import com.stripe.payment_service_provider.payment.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.repository.TransactionRepository;
import com.stripe.payment_service_provider.subscription.model.StripePlan;
import com.stripe.payment_service_provider.subscription.model.UserSubscription;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.payment.repository.StripeCustomerRepository;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceEventHandler;
import com.stripe.payment_service_provider.subscription.service.impl.UserSubscriptionServiceImpl;
import com.stripe.payment_service_provider.settings.exceptions.stripe.CustomerNotFoundException;
import com.stripe.payment_service_provider.user.model.User;
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
    private final StripeCustomerRepository stripeCustomerRepository;
    private final SubscriptionEmailService subscriptionEmailService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final TransactionRepository transactionRepository;
    private final UserSubscriptionServiceImpl subscriptionService;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    public void handleUpcomingInvoice(Invoice invoice) {
        User user = userRepository.findByCustomerId(invoice.getCustomer())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));

        UserSubscription userSubscription = subscriptionService.getActiveUserSubscription(user.getStripeCustomer().getSubscriptions())
                .orElseThrow(() -> new ResourceNotFoundException("user subscriptions not found"));

        subscriptionEmailService.sendSubscriptionExpirationNotification(
                user.getEmail(),
                userSubscription
        );
    }

    @Transactional
    @Override
    public void handleInvoicePaymentUpdate(Invoice invoice) throws StripeException {
        String billingReason = invoice.getBillingReason();
        String customerId = invoice.getCustomer();

        InvoiceRetrieveParams params = InvoiceRetrieveParams.builder()
                .addExpand("payments.data.payment.payment_intent").build();
        Invoice expandedInvoice = Invoice.retrieve(invoice.getId(), params, null);

        PaymentIntent paymentIntent = expandedInvoice.getPayments().getData().getFirst().getPayment().getPaymentIntentObject();

        StripeCustomer stripeCustomer = stripeCustomerRepository.findStripeCustomerByStripeCustomerId(customerId)
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
                transactionRepository.save(stripePayment);

                subscriptionEmailService.sendSubscriptionSuccessEmail(
                        stripeCustomer.getEmail(),
                        userSubscription,
                        invoice
                );
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
                transactionRepository.save(stripePayment);

                subscriptionEmailService.sendSubscriptionUpdateEmail(
                        stripeCustomer.getEmail(),
                        userSubscription,
                        stripePlan.getName()
                );
                break;
            }

            case "subscription_cycle": {
              /* subscriptionEmailService.sendSubscriptionRenewalEmail(
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
            StripePaymentMethod stripePaymentMethod
    ) {
        return StripePayment.builder()
                .customer(stripeCustomer)
                .paymentIntentId(paymentIntent.getId())
                .invoice(stripeInvoice)
                .paymentMethod(stripePaymentMethod)
                .amountPaid(BigDecimal.valueOf(paymentIntent.getAmount()))
                .currency(paymentIntent.getCurrency().toUpperCase())
                .status(PaymentStatus.SUCCEEDED)
                .build();
    }
}
