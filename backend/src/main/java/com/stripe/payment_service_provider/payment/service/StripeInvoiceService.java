package com.stripe.payment_service_provider.payment.service;

import com.stripe.payment_service_provider.payment.dto.InvoiceDTO;
import com.stripe.payment_service_provider.payment.dto.payment.PaymentRequestDTO;
import com.stripe.exception.StripeException;

import java.util.List;

public interface StripeInvoiceService {
    List<InvoiceDTO> getAllInvoices(PaymentRequestDTO requestDTO) throws StripeException;
    List<InvoiceDTO> getAllByCustomerId(String customerId) throws StripeException;
}
