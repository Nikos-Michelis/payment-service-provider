package com.stripe.payment_service_provider.payment.producer.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;

public interface StripeInvoiceEventHandler {
    void handleUpcomingInvoice(Invoice invoice);
    void handleInvoicePayment(Invoice invoice) throws StripeException;
}
