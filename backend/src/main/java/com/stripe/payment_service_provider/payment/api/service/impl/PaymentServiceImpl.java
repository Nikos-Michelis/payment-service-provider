package com.stripe.payment_service_provider.payment.api.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.payment_service_provider.payment.api.dto.payment.request.OneOffPaymentRequest;
import com.stripe.payment_service_provider.payment.api.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.api.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.payment.api.util.CheckoutSessionUtil;
import com.stripe.payment_service_provider.payment.api.util.CustomerUtil;
import com.stripe.payment_service_provider.payment.api.service.PaymentService;
import com.stripe.payment_service_provider.products.model.*;
import com.stripe.payment_service_provider.products.repository.AddressRepository;
import com.stripe.payment_service_provider.products.repository.CartRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.repository.ShipperRatesRepository;
import com.stripe.payment_service_provider.products.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final CustomerUtil customerUtil;
    private final ProductRepository productRepository;
    private final CustomerPortalRepository customerPortalRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;
    private final ShipperRatesRepository shipperRatesRepository;
    private final AddressRepository addressRepository;

    @Transactional
    @Override
    public String createOneOffPayment(String email, String idempotencyKey, OneOffPaymentRequest oneOffPaymentRequest) throws StripeException {

        Optional<CustomerPortal> existingSession = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (existingSession.isPresent()) {
            return existingSession.get().getSessionUrl();
        }

        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(email);

        Address address = addressRepository.findAddressByUuidAndUser_id(oneOffPaymentRequest.addressUUID(), stripeCustomer.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Set<CartItem> cartItems = retrieveCartItems(stripeCustomer);

        ShippingRates shippingRates = shipperRatesRepository.findShipperByUuidAndCountryCode(oneOffPaymentRequest.shipperUUID(), oneOffPaymentRequest.countryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Shipper not found"));

        Set<OrderItem> orderItems = orderService.buildOrderItems(cartItems);
        Order order = orderService.createOrder(stripeCustomer, orderItems, shippingRates.getTotal(), address);

        SessionCreateParams params = buildSessionParams(stripeCustomer, shippingRates, orderItems, order);
        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Session session = Session.create(params, requestOptions);

        CustomerPortal customerPortal = buildCustomerPortal(stripeCustomer, session, idempotencyKey);
        customerPortalRepository.save(customerPortal);

        return session.getUrl();
    }

    private Set<CartItem> retrieveCartItems(StripeCustomer stripeCustomer) {
        Cart cart = cartRepository.findCartByUser_id(stripeCustomer.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Set<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Empty cart items");
        }

        return cartItems;
    }

    private SessionCreateParams buildSessionParams(StripeCustomer stripeCustomer, ShippingRates shippingRates, Set<OrderItem> orderItems, Order order) {
        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(
                SessionCreateParams.Mode.PAYMENT,
                stripeCustomer.getStripeCustomerId()
        );

        paramsBuilder.addShippingOption(checkoutSessionUtil.buildShippingOption(shippingRates));
        paramsBuilder.addAllLineItem(buildLineItemCollection(orderItems));
        paramsBuilder.setPaymentIntentData(
                SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("order_id", order.getUuid())
                        .build()
        );

        return paramsBuilder.build();
    }

    private CustomerPortal buildCustomerPortal(StripeCustomer stripeCustomer, Session session, String idempotencyKey) {
        return CustomerPortal.builder()
                .customer(stripeCustomer)
                .idempotencyKey(idempotencyKey)
                .sessionType(session.getObject())
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    private List<SessionCreateParams.LineItem> buildLineItemCollection(Set<OrderItem> orderItems){
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (var cartItem : orderItems) {
            Product product = productRepository.findProductBySku(cartItem.getProduct().getSku())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            SessionCreateParams.LineItem lineItem = buildLineItem(product, cartItem.getQuantity());
            lineItems.add(lineItem);
        }
        return lineItems;
    }

    private SessionCreateParams.LineItem buildLineItem(Product product, long quantity) {
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(product.getTitle())
                        .setDescription(product.getDescription())
                        .addAllImage(product.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList()))
                        .putMetadata("sku", product.getSku())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setUnitAmount(product.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                        .setCurrency("EUR")
                        .setProductData(productData)
                        .build();

        return SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(quantity)
                .build();
    }
}
