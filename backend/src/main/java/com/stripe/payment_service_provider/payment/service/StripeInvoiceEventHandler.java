package com.stripe.payment_service_provider.payment.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;

public interface StripeInvoiceEventHandler {
    void handleUpcomingInvoice(Invoice invoice);
    void handleInvoicePaymentUpdate(Invoice invoice) throws StripeException;
}
