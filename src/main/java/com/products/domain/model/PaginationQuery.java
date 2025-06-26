package com.products.domain.model;

public record PaginationQuery(int pageNumber, int pageSize, String sortBy, String sortDir) {

    public PaginationQuery {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be negative.");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be at least 1.");
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        if (sortDir == null || sortDir.isBlank()) {
            sortDir = "asc";
        }
    }

    public PaginationQuery(int pageNumber, int pageSize) {
        this(pageNumber, pageSize, "id", "asc");
    }
}