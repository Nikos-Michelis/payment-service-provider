package com.stripe.payment_service_provider.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageSortingDTO {
    private Integer page;
    private Integer limit;
    private String field;
    private String sort;
}