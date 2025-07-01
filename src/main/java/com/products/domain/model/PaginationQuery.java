package com.products.domain.model;

public record PaginationQuery(String cursor, int limit, String sortBy, String sortDir) {

    public PaginationQuery {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("Limit must be between 1 and 100.");
        }
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        if (sortDir == null || sortDir.isBlank()) {
            sortDir = "asc";
        }
    }

    public PaginationQuery(String cursor, int limit) {
        this(cursor, limit, "id", "asc");
    }

    public PaginationQuery(int limit) {
        this(null, limit, "id", "asc");
    }
}