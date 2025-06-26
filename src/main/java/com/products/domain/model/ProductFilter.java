package com.products.domain.model;

public record ProductFilter(String category, String name, Boolean active) {

    public ProductFilter {
        active = active != null ? active : true;
    }

    public boolean hasCategory() {
        return category != null && !category.trim().isEmpty();
    }

    public boolean hasName() {
        return name != null && !name.trim().isEmpty();
    }

    public String getCategoryForQuery() {
        return hasCategory() ? category : null;
    }

    public String getNameForQuery() {
        return hasName() ? name : null;
    }
}