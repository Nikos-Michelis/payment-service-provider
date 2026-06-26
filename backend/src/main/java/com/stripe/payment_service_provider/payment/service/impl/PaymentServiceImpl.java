package com.stripe.payment_service_provider.payment.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.payment_service_provider.payment.dto.payment.request.OrderLineRequestDTO;
import com.stripe.payment_service_provider.payment.dto.payment.response.SessionResponseDTO;
import com.stripe.payment_service_provider.payment.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.payment.service.PaymentService;
import com.stripe.payment_service_provider.payment.util.CheckoutSessionUtil;
import com.stripe.payment_service_provider.payment.util.CustomerUtil;
import com.stripe.payment_service_provider.payment.util.ProductUtil;
import com.stripe.payment_service_provider.payment.util.SubscriptionUtil;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.model.ProductImage;
import com.stripe.payment_service_provider.subscription.repository.PlanRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
                        .setUnitAmountDecimal(product.getPrice())
                        .setProductData(productData)
                        .build();

        return SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(quantity)
                .build();
    }
}
