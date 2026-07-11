package com.stripe.payment_service_provider.products.controller;

import com.stripe.payment_service_provider.products.dto.PageSortingDTO;
import com.stripe.payment_service_provider.products.dto.ProductDTO;
import com.stripe.payment_service_provider.products.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("public")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;
    private final int MAX_ITEMS = 50;

    @GetMapping("/products")
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> getAllProducts(
            @RequestParam(required = false) Map<String, String> requestParams,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "12") Integer limit,
            @RequestParam(defaultValue = "rating") String field,
            @RequestParam(defaultValue = "asc") String ordering,
            PagedResourcesAssembler<ProductDTO> assembler) {


        return ResponseEntity
                .ok(assembler.toModel(productService.searchProducts(requestParams,
                        PageSortingDTO.builder()
                                .page(page)
                                .limit(limit >= MAX_ITEMS ? MAX_ITEMS : limit)
                                .field(field)
                                .sort(ordering)
                                .build())));
    }
}
