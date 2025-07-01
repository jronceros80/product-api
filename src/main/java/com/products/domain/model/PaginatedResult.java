package com.products.domain.model;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        String nextCursor,
        String previousCursor,
        boolean hasNext,
        boolean hasPrevious,
        int size,
        int limit) {
}