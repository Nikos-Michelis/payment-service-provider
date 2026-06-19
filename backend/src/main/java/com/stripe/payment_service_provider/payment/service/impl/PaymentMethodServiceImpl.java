package com.stripe.payment_service_provider.payment.service.impl;

import com.stripe.model.PaymentMethod;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.model.StripePaymentMethod;
import com.stripe.payment_service_provider.payment.repository.PaymentMethodRepository;
import com.stripe.payment_service_provider.payment.repository.StripeCustomerRepository;
import com.stripe.payment_service_provider.payment.service.StripePaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMethodServiceImpl implements StripePaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final StripeCustomerRepository  stripeCustomerRepository;

    @Override
    @Transactional
    public StripePaymentMethod addPaymentMethod(PaymentMethod paymentMethod) {

        StripeCustomer stripeCustomer = stripeCustomerRepository.findStripeCustomerByStripeCustomerId(paymentMethod.getCustomer())
                .orElseThrow(() -> new ResourceNotFoundException("Stripe customer not found"));

        Optional<StripePaymentMethod> stripePaymentMethod = paymentMethodRepository.findStripePaymentMethodByPaymentMethodIdAndCustomerId(paymentMethod.getCard().getFingerprint(), stripeCustomer.getCustomerId());

        if (stripePaymentMethod.isPresent()) {
            throw new RuntimeException("Stripe payment method already assign");
        }

        setDefaultPaymentMethod(stripeCustomer.getCustomerId());

        StripePaymentMethod newStripePaymentMetho = StripePaymentMethod.builder()
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

        return paymentMethodRepository.save(newStripePaymentMetho);
    }

    private void setDefaultPaymentMethod(Long customerId) {
        List<StripePaymentMethod> stripePaymentMethod = paymentMethodRepository.findAllByCustomer_CustomerId(customerId);

        for (StripePaymentMethod paymentMethod : stripePaymentMethod) {
            paymentMethod.setIsDefault(false);
        }

        paymentMethodRepository.saveAll(stripePaymentMethod);
    }
}
