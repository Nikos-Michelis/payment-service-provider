package com.stripe.payment_service_provider.payment.api.service.impl;

import com.stripe.payment_service_provider.payment.api.model.StripeInvoice;
import com.stripe.payment_service_provider.payment.api.dto.InvoiceDTO;
import com.stripe.payment_service_provider.payment.api.dto.payment.request.SubscriptionRequestDTO;
import com.stripe.payment_service_provider.payment.api.repository.InvoiceRepository;
import com.stripe.payment_service_provider.payment.api.service.InvoiceService;
import com.stripe.payment_service_provider.utils.DtoConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final DtoConverter dtoConverter;
    private final InvoiceRepository invoiceRepository;

    @Override
    public List<InvoiceDTO> getAllInvoices(SubscriptionRequestDTO requestDTO) {
        List<StripeInvoice> stripeInvoice = invoiceRepository.findAll();
        return stripeInvoice
                .stream()
                .map(invoice -> dtoConverter.convertToDto(invoice, InvoiceDTO.class))
                .toList();
    }

    @Override
    public List<InvoiceDTO> getAllByCustomerId(String customerId) {
        List<StripeInvoice> stripeInvoice = invoiceRepository.findStripeInvoiceBySubscription_Customer_StripeCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Stripe invoice not found for Customer Id: " + customerId));
        return stripeInvoice
                .stream()
                .map(invoice -> dtoConverter.convertToDto(invoice, InvoiceDTO.class))
                .toList() ;
    }
}
