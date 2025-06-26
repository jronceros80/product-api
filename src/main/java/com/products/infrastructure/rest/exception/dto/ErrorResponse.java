package com.products.infrastructure.rest.exception.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp,
    Map<String, List<String>> errors
) {
    public ErrorResponse(String message, int status) {
        this(message, status, LocalDateTime.now(), null);
    }

    public ErrorResponse(String message, int status, Map<String, List<String>> errors) {
        this(message, status, LocalDateTime.now(), errors);
    }
}
