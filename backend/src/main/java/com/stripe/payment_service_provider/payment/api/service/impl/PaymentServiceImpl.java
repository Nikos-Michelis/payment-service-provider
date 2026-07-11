package com.stripe.payment_service_provider.payment.api.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.payment_service_provider.payment.api.model.CustomerPortal;
import com.stripe.payment_service_provider.payment.api.model.StripeCustomer;
import com.stripe.payment_service_provider.payment.api.repository.CustomerPortalRepository;
import com.stripe.payment_service_provider.payment.api.util.CheckoutSessionUtil;
import com.stripe.payment_service_provider.payment.api.util.CustomerUtil;
import com.stripe.payment_service_provider.payment.api.service.PaymentService;
import com.stripe.payment_service_provider.products.dto.TotalCostDTO;
import com.stripe.payment_service_provider.products.dto.TotalItemCost;
import com.stripe.payment_service_provider.products.model.*;
import com.stripe.payment_service_provider.products.repository.CartRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private final CheckoutSessionUtil checkoutSessionUtil;
    private final CustomerUtil customerUtil;
    private final ProductRepository productRepository;
    private final CustomerPortalRepository customerPortalRepository;
    private final CartRepository cartRepository;
    private final OrderService orderService;

    @Transactional
    @Override
    public String createOneOffPayment(String email, String idempotencyKey) throws StripeException {

        Optional<CustomerPortal> existingSession = customerPortalRepository.findCustomerPortalByIdempotencyKey(idempotencyKey);
        if (existingSession.isPresent()) {
            return existingSession.get().getSessionUrl();
        }

        StripeCustomer stripeCustomer = customerUtil.findOrCreateStripeCustomer(email);

        Cart cart = cartRepository.findCartByUser_id(stripeCustomer.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Set<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Order line not found");
        }

        SessionCreateParams.Builder paramsBuilder = checkoutSessionUtil.buildCheckoutSession(
                SessionCreateParams.Mode.PAYMENT,
                stripeCustomer.getStripeCustomerId()
        );
        // TODO add shipping providers and supported countries
        paramsBuilder.setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED);
        paramsBuilder.setShippingAddressCollection(
                SessionCreateParams.ShippingAddressCollection.builder()
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.US)
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.GR)
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.FR)
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.GE)
                        .build()
        );
        SessionCreateParams.ShippingOption shippingOption = checkoutSessionUtil.buildShippingOption();
        paramsBuilder.addShippingOption(shippingOption);

        paramsBuilder.setCustomerUpdate(
                SessionCreateParams.CustomerUpdate.builder()
                        .setShipping(SessionCreateParams.CustomerUpdate.Shipping.AUTO)
                        .build()
        );

        Set<OrderItem> orderItems = buildOrderItems(cartItems);

        for (var cartItem : cartItems) {
            Product product = productRepository.findProductBySku(cartItem.getProduct().getSku())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            SessionCreateParams.LineItem lineItem = buildLineItem(product, cartItem.getQuantity());
            paramsBuilder.addLineItem(lineItem);
        }

        orderService.createOrder(stripeCustomer, orderItems);

        RequestOptions requestOptions = checkoutSessionUtil.getIdempotencyKey(idempotencyKey);
        Session session = Session.create(paramsBuilder.build(), requestOptions);

        CustomerPortal customerPortal = buildCustomerPortal(stripeCustomer, session, idempotencyKey);
        customerPortalRepository.save(customerPortal);

        return session.getUrl();
    }

    private Set<OrderItem> buildOrderItems(Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findProductBySku(cartItem.getProduct().getSku())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int quantity = cartItem.getQuantity();
            TotalItemCost itemCost = calculateItemCost(product, quantity);
            OrderItem orderItem = buildOrderItem(product, quantity, itemCost.total());
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private OrderItem buildOrderItem(Product product, int quantity, BigDecimal totalAmount) {
        return OrderItem.builder()
                .product(product)
                .amount(product.getPrice())
                .discount(product.getDiscountPercentage())
                .tax(product.getTax())
                .quantity(quantity)
                .total(totalAmount)
                .build();
    }

    private TotalItemCost calculateItemCost(Product product, Integer quantity) {
        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        BigDecimal discountRate = BigDecimal.valueOf(product.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);

        BigDecimal discount = subtotal.multiply(discountRate);

        BigDecimal taxableAmount = subtotal.subtract(discount);

        BigDecimal taxRate = BigDecimal.valueOf(product.getTax())
                .divide(BigDecimal.valueOf(100), SCALE, ROUNDING);

        BigDecimal tax = taxableAmount.multiply(taxRate);

        BigDecimal total = taxableAmount.add(tax);
        return new TotalItemCost(subtotal, discount, tax, total);
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
                        .setUnitAmount(
                                product.getPrice().multiply(BigDecimal.valueOf(100)).longValue()
                        )
                        .setCurrency("USD")
                        .setProductData(productData)
                        .build();

        return SessionCreateParams.LineItem.builder()
                .setPriceData(priceData)
                .setQuantity(quantity)
                .build();
    }
}
