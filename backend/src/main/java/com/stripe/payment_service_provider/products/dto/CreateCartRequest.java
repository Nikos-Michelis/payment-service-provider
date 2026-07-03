package com.stripe.payment_service_provider.products.dto;

import java.util.List;

public record CreateCartRequest (List<CartItemDTO> items){}