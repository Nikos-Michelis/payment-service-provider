package com.stripe.payment_service_provider.payment.api.service.impl;

import com.stripe.model.Event;
import com.stripe.model.PaymentMethod;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.api.model.StripePaymentMethod;
import com.stripe.payment_service_provider.payment.api.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.api.repository.CustomerRepository;
import com.stripe.payment_service_provider.payment.api.service.PaymentMethodService;
import com.stripe.payment_service_provider.settings.exceptions.common.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void addPaymentMethod(Event event) {
        PaymentMethod paymentMethod = handlePaymentMethodEvent(event);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(paymentMethod.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("Stripe customer not found"));

        Optional<StripePaymentMethod> stripePaymentMethod = paymentMethodRepository.findStripePaymentMethodByPaymentMethodIdAndCustomerId(paymentMethod.getCard().getFingerprint(), stripeCustomer.getCustomerId());

        if (stripePaymentMethod.isPresent()) {
            throw new ConflictException("Stripe payment method already assign");
        }

        revokePaymentMethods(stripeCustomer.getCustomerId());
        StripePaymentMethod newStripePaymentMethod = buildStripePaymentMethod(stripeCustomer, paymentMethod);

        paymentMethodRepository.save(newStripePaymentMethod);
    }

    private StripePaymentMethod buildStripePaymentMethod(StripeCustomer stripeCustomer, PaymentMethod paymentMethod){
        return StripePaymentMethod.builder()
                .customer(stripeCustomer)
                .stripePaymentMethodId(paymentMethod.getId())
                .fingerprint(paymentMethod.getCard().getFingerprint())
                .last4(paymentMethod.getCard().getLast4())
                .brand(paymentMethod.getCard().getBrand())
                .funding(paymentMethod.getCard().getFunding())
                .type(paymentMethod.getType())
                .expMonth(paymentMethod.getCard().getExpMonth().intValue())
                .exoYear(paymentMethod.getCard().getExpYear().intValue())
                .isDefault(true)
                .build();
    }

    private void revokePaymentMethods(Long customerId) {
        List<StripePaymentMethod> stripePaymentMethod = paymentMethodRepository.findAllByCustomer_CustomerId(customerId);

        for (StripePaymentMethod paymentMethod : stripePaymentMethod) {
            paymentMethod.setIsDefault(false);
        }

        paymentMethodRepository.saveAll(stripePaymentMethod);
    }

    @Override
    @Transactional
    public void removePaymentMethod(Event event) {
        PaymentMethod paymentMethod = handlePaymentMethodEvent(event);

        StripeCustomer stripeCustomer = customerRepository.findStripeCustomerByStripeCustomerId(paymentMethod.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("Stripe customer not found"));

        StripePaymentMethod stripePaymentMethod = paymentMethodRepository.findStripePaymentMethodByPaymentMethodIdAndCustomerId(paymentMethod.getCard().getFingerprint(), stripeCustomer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        stripePaymentMethod.setIsDefault(false);

        paymentMethodRepository.save(stripePaymentMethod);
    }

    private PaymentMethod handlePaymentMethodEvent(Event event) {
        return (PaymentMethod) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found"));
    }
}
