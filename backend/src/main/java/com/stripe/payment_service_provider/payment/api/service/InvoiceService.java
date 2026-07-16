package com.stripe.payment_service_provider.payment.api.service;

import com.stripe.payment_service_provider.payment.api.dto.InvoiceDTO;
import com.stripe.payment_service_provider.payment.api.dto.payment.request.SubscriptionRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDTO> getAllInvoices(SubscriptionRequestDTO requestDTO) throws StripeException;
    List<InvoiceDTO> getAllByCustomerId(String customerId) throws StripeException;
}
