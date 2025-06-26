package com.products.infrastructure.dto;

import com.products.domain.model.ProductCategory;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        ProductCategory category,
        Boolean active) {
}