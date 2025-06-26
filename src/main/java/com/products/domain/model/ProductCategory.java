package com.products.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCategory {
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    BOOKS("Books");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

}