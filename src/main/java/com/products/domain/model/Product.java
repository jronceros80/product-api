package com.products.domain.model;

import java.math.BigDecimal;

public record Product(
    Long id,
    String name,
    BigDecimal price,
    ProductCategory category,
    Boolean active
) {
    public Product(String name, BigDecimal price, ProductCategory category) {
        this(null, name, price, category, true);
    }

    public Product(Long id, String name, BigDecimal price, ProductCategory category) {
        this(id, name, price, category, true);
    }
}