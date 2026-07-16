package com.stripe.payment_service_provider.products.service.impl;

import com.stripe.payment_service_provider.products.dto.CartDTO;
import com.stripe.payment_service_provider.products.dto.TotalCost;
import com.stripe.payment_service_provider.products.dto.request.CartItemRequest;
import com.stripe.payment_service_provider.products.mappers.CartObjectMapper;
import com.stripe.payment_service_provider.products.model.Cart;
import com.stripe.payment_service_provider.products.model.CartItem;
import com.stripe.payment_service_provider.products.model.Product;
import com.stripe.payment_service_provider.products.repository.CartItemRepository;
import com.stripe.payment_service_provider.products.repository.CartRepository;
import com.stripe.payment_service_provider.products.repository.ProductRepository;
import com.stripe.payment_service_provider.products.service.CartService;
import com.stripe.payment_service_provider.products.service.PricingService;
import com.stripe.payment_service_provider.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PricingService pricingService;
    private final CartObjectMapper cartObjectMapper;

    @Override
    public CartDTO getAllUserCartItems(User user) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseGet(() -> Cart.builder().user(user).build());

        CartDTO cartDTO = cartObjectMapper.toDto(cart);

        int totalItems = cart.getCartItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        TotalCost totalCost = pricingService.calculateTotal(cart.getCartItems());
        return new CartDTO(cartDTO.uuid(), totalItems, totalCost.subtotal(), totalCost.total(), cartDTO.cartItems());
    }

    @Transactional
    public CartDTO addItemToCart(User user, CartItemRequest item) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseGet(() -> Cart.builder().user(user).build());

        Product product = productRepository.findProductBySku(item.sku())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStock() == 0) {
            throw new RuntimeException("Product" + item.sku() + " out of stock.");
        }

        if (product.getStock() < item.quantity()) {
            throw new RuntimeException("Requested quantity for "
                    + product.getTitle() + " is not available, please decrease the quantity.");
        }

        CartItem orderItem = cartItemRepository.findOrderLineByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> buildOrderLine(product, item.quantity()));

        if (product.getStock() < orderItem.getQuantity()) {
            throw new RuntimeException("Requested quantity for "
                    + product.getTitle() + " is not available, please decrease the quantity.");
        }

        orderItem.setQuantity(orderItem.getQuantity() + item.quantity());
        cart.addCartLine(orderItem);

        cartRepository.save(cart);

        return cartObjectMapper.toDto(cart);
    }

    @Transactional
    public CartDTO removeItemFromCart(User user, UUID orderLineUUID) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository.findOrderLineByUuidAndCart_Id(orderLineUUID, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.removeOrderLine(cartItem);
        cartRepository.save(cart);
        return cartObjectMapper.toDto(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public CartDTO updateItemQuantity(User user, CartItemRequest item, UUID orderLineUUID) {
        Cart cart = cartRepository.findCartByUser_id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        CartItem cartItem = cartItemRepository.findOrderLineByUuidAndCart_Id(orderLineUUID, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order line not found"));

        Product product = productRepository.findProductBySku(item.sku())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (product.getStock() == 0) {
            throw new RuntimeException("Product" + item.sku() + " out of stock.");
        }

        if (product.getStock() < item.quantity()) {
            throw new RuntimeException("Requested quantity for "
                    + product.getSku() + " is not available, please decrease the quantity.");
        }

        if (product.getStock() < cartItem.getQuantity()) {
            throw new RuntimeException("Requested quantity for "
                    + product.getSku() + " is not available, please decrease the quantity.");
        }

        cartItem.setQuantity(item.quantity());
        cartItemRepository.save(cartItem);
        return cartObjectMapper.toDto(cart);
    }

    private CartItem buildOrderLine(Product product, Integer quantity) {
        return CartItem.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }
}
