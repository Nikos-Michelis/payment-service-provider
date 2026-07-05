package com.stripe.payment_service_provider.payment.api.util;

import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.param.InvoiceRetrieveParams;
import com.stripe.payment_service_provider.settings.exceptions.stripe.CustomStripeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class InvoiceUtil {

    public Invoice getInvoiceById(String invoiceId)  {
        try {
            InvoiceRetrieveParams invoiceRetrieveParams = InvoiceRetrieveParams.builder()
                    .addExpand("payments.data.payment.payment_intent")
                    .build();

            return Invoice.retrieve(invoiceId, invoiceRetrieveParams, null);
        } catch (StripeException e) {
            throw new CustomStripeException(
                    "Oops! Something went wrong", "Failed to retrive invoice." + e.getMessage(), HttpStatus.valueOf(e.getStatusCode()));
        }
    }

}
