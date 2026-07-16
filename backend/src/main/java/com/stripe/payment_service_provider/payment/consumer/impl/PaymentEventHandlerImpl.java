package com.stripe.payment_service_provider.payment.consumer.impl;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.payment_service_provider.payment.consumer.PaymentIntentHandler;
import com.stripe.payment_service_provider.payment.consumer.state.PaymentStateMachine;
import com.stripe.payment_service_provider.payment.api.model.*;
import com.stripe.payment_service_provider.payment.api.repository.CustomerRepository;
import com.stripe.payment_service_provider.payment.api.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.api.repository.PaymentRepository;
import com.stripe.payment_service_provider.settings.exceptions.common.InvalidStateTransitionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventHandlerImpl implements PaymentIntentHandler {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final PaymentStateMachine stateMachine;


    @Transactional
    @Override
    public void onPaymentIntentCreate(Event event) {
        PaymentIntent paymentIntent = handlePaymentIntentEvent(event);
        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(paymentIntent.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("No customer found with id: " + paymentIntent.getCustomer()));

        StripePayment stripePayment = buildPaymentIntent(paymentIntent, stripeCustomer);
        paymentRepository.save(stripePayment);
    }

    @Transactional
    @Override
    public void onPaymentIntentUpdate(Event event, PaymentStatus status) throws InvalidStateTransitionException, ResourceNotFoundException {
        PaymentIntent paymentIntent = handlePaymentIntentEvent(event);

        StripePayment stripePayment = paymentRepository.findStripePaymentByPaymentIntentId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment intent not found with id: " + paymentIntent.getId()));

        Optional<StripePaymentMethod> stripePaymentMethod =
                paymentMethodRepository.findPaymentMethodByStripePaymentMethodId(paymentIntent.getPaymentMethod());

        PaymentStatus currentStatus = stripePayment.getStatus();

        if (!stateMachine.canTransition(currentStatus, status)) {
            throw new InvalidStateTransitionException("Invalid transition [" + currentStatus + "] for payment [" + paymentIntent.getId() + "]");
        }

        stripePaymentMethod.ifPresent(stripePayment::setPaymentMethod);
        stripePayment.setStatus(status);
        paymentRepository.save(stripePayment);
    }

    private StripePayment buildPaymentIntent(PaymentIntent paymentIntent, StripeCustomer stripeCustomer) {
        return StripePayment.builder()
                .customer(stripeCustomer)
                .paymentIntentId(paymentIntent.getId())
                .amountPaid(BigDecimal.valueOf(paymentIntent.getAmount()))
                .currency(paymentIntent.getCurrency().toUpperCase())
                .status(PaymentStatus.CREATED)
                .build();
    }

    private PaymentIntent handlePaymentIntentEvent(Event event) {
        return (PaymentIntent) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("PaymentIntent not found"));
    }
}
