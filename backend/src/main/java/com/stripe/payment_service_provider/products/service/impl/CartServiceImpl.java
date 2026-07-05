package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.CartItemDTO;
import com.stripe.payment_service_provider.products.dto.CreateCartRequest;
import com.stripe.payment_service_provider.products.mappers.CartObjectMapper;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.OrderLine;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.repository.CartRepository;
import com.stripe.payment_service_provider.products.repository.OrderLineRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.service.CartService;
import com.stripe.payment_service_provider.user.model.User;
import com.stripe.payment_service_provider.utils.DtoConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderLineRepository orderLineRepository;
    private final CartObjectMapper cartObjectMapper;

    @Transactional
    public CartDTO addItemToCart(User user, CreateCartRequest createCartRequest) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseGet(() -> Cart.builder().user(user).build());

        List<CartItemDTO> cartItems = createCartRequest.items();

        for (var item : cartItems) {
            Product product = productRepository.findProductBySku(item.sku())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

            if (product.getStock() == 0) {
                throw new RuntimeException("Products out of stock.");
            }

            if (product.getStock() < item.quantity()) {
                throw new RuntimeException("Requested quantity for "
                        + product.getTitle() + " is not available, please decrease the quantity.");
            }

            OrderLine orderLine = orderLineRepository.findOrderLineByProduct_Id(product.getId())
                    .orElseGet(() -> buildOrderLine(product, item.quantity()));
            orderLine.setQuantity(item.quantity());
            cart.addOrderLine(orderLine);
        }

        cartRepository.save(cart);

        return cartObjectMapper.toDto(cart);
    }

    @Transactional
    public void removeItemFromCart(User user, long itemId) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseGet(() -> Cart.builder().user(user).build());

        OrderLine orderLine = orderLineRepository.findOrderLineByProduct_Id(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        cart.removeOrderLine(orderLine);
        cartRepository.save(cart);
    }

    @Transactional
    public void updateOrderLineQuantity(Long orderLineId, int newQuantity) {
        OrderLine orderLine = orderLineRepository.findById(orderLineId)
                .orElseThrow(() -> new EntityNotFoundException("Order line not found"));

        Product product = orderLine.getProduct();

        if (product.getStock() < newQuantity) {
            throw new RuntimeException("Requested quantity for "
                    + product.getTitle() + " is not available, please decrease the quantity.");
        }

        orderLine.setQuantity(newQuantity);
        orderLine.setTotal(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        orderLineRepository.save(orderLine);
    }


    private OrderLine buildOrderLine(Product product, Integer quantity) {
        return OrderLine.builder()
                .product(product)
                .amount(product.getPrice())
                .discount(product.getDiscountPercentage())
                .taxAmount(BigDecimal.valueOf(22))
                .total(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }
}
