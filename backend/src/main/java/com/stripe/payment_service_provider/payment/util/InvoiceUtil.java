package com.stripe.payment_service_provider.payment.util;

import com.stripe.model.Invoice;
import com.stripe.param.InvoiceRetrieveParams;
import org.springframework.stereotype.Component;

@Component
public class InvoiceUtil {

   /* InvoiceRetrieveParams params = InvoiceRetrieveParams.builder().addExpand("payments.data.payment.payment_intent").build();
    Invoice expandedInvoice = Invoice.retrieve(invoice.getId(), params, null);*/
}
