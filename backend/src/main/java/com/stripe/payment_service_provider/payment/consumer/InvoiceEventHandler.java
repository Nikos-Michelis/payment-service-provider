package com.stripe.payment_service_provider.payment.consumer;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;

public interface InvoiceEventHandler {
    void onInvoiceUpcoming(Event event);
    void onInvoicePaid(Event event) throws StripeException;
    void onInvoiceUpdate(Event event) throws StripeException;
    void onInvoicePaymentPaid(Event event) throws StripeException;
}
