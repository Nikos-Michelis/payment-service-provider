package com.stripe.payment_service_provider.payment.consumer.service;

import com.stripe.payment_service_provider.payment.consumer.dto.InvoiceDTO;
import com.stripe.payment_service_provider.payment.consumer.dto.payment.request.SubscriptionRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;

public interface StripeInvoiceService {
    List<InvoiceDTO> getAllInvoices(SubscriptionRequestDTO requestDTO) throws StripeException;
    List<InvoiceDTO> getAllByCustomerId(String customerId) throws StripeException;
}
