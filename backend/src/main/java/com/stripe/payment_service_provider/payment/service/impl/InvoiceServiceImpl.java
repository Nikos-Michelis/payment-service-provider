package com.stripe.payment_service_provider.payment.service.impl;

import com.stripe.payment_service_provider.payment.model.StripeInvoice;
import com.stripe.payment_service_provider.payment.dto.InvoiceDTO;
import com.stripe.payment_service_provider.payment.dto.payment.PaymentRequestDTO;
import com.stripe.payment_service_provider.payment.repository.StripeInvoiceRepository;
import com.stripe.payment_service_provider.payment.service.StripeInvoiceService;
import com.stripe.payment_service_provider.utils.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements StripeInvoiceService {
    private final DtoConverter dtoConverter;
    private final StripeInvoiceRepository stripeInvoiceRepository;

    @Override
    public List<InvoiceDTO> getAllInvoices(PaymentRequestDTO requestDTO) {
        List<StripeInvoice> stripeInvoice = stripeInvoiceRepository.findAll();
        return stripeInvoice
                .stream()
                .map(invoice -> dtoConverter.convertToDto(invoice, InvoiceDTO.class))
                .toList();
    }

    @Override
    public List<InvoiceDTO> getAllByCustomerId(String customerId) {
        List<StripeInvoice> stripeInvoice = stripeInvoiceRepository.findStripeInvoiceBySubscription_StripeCustomer_StripeCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Stripe invoice not found for Customer Id: " + customerId));
        return stripeInvoice
                .stream()
                .map(invoice -> dtoConverter.convertToDto(invoice, InvoiceDTO.class))
                .toList() ;
    }
}
