package com.stripe.payment_service_provider.payment.consumer.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.payment_service_provider.payment.consumer.dto.payment.request.OrderLineRequestDTO;
import com.stripe.payment_service_provider.payment.consumer.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.consumer.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.consumer.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.payment.consumer.service.PaymentService;
import com.stripe.payment_service_provider.payment.consumer.util.CheckoutSessionUtil;
import com.stripe.payment_service_provider.payment.consumer.util.CustomerUtil;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.model.ProductImage;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final CustomerUtil customerUtil;
    private final ProductRepository productRepository;
    private final CustomerPortalRepository customerPortalRepository;

    @Transactional
    @Override
    public String createOneTimePayment(List<OrderLineRequestDTO> orderLines, String email, String idempotencyKey) throws StripeException {

        Optional<CustomerPortal> existingSession = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (existingSession.isPresent()) {
            return existingSession.get().getSessionUrl();
        }

        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(email);

        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(
                SessionCreateParams.Mode.PAYMENT,
                stripeCustomer.getStripeCustomerId()
        );
        paramsBuilder.setInvoiceCreation(
                SessionCreateParams.InvoiceCreation.builder()
                        .setEnabled(true)
                        .build()
        );


        for (OrderLineRequestDTO orderLine : orderLines) {
            Product product = productRepository.findProductBySku(orderLine.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            SessionCreateParams.LineItem lineItem = buildLineItem(product, orderLine.quantity());
            paramsBuilder.addLineItem(lineItem);
        }

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Session session = Session.create(paramsBuilder.build(), requestOptions);

        CustomerPortal customerPortal = CustomerPortal.builder()
                .customer(stripeCustomer)
                .idempotencyKey(idempotencyKey)
                .sessionType(session.getObject())
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();

        customerPortalRepository.save(customerPortal);

        return session.getUrl();
    }


    private SessionCreateParams.LineItem buildLineItem(Product product, long quantity) {
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(product.getTitle())
                        .setDescription(product.getDescription())

                        .addAllImage(product.getProductImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()))
                        .putMetadata("sku", product.getSku())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setUnitAmount(
                                product.getPrice().multiply(BigDecimal.valueOf(100)).longValue()
                        )                        .setCurrency("USD")
                        .setProductData(productData)
                        .build();

        return SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(quantity)
                .build();
    }
}
