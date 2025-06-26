package com.products.infrastructure.rest.exception.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void createErrorResponse_WithAllFields_ShouldSetCorrectValues() {
        String message = "Test error message";
        int status = 400;
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("field1", List.of("error1"));
        errors.put("field2", List.of("error2"));

        ErrorResponse errorResponse = new ErrorResponse(message, status, errors);

        assertThat(errorResponse.message()).isEqualTo(message);
        assertThat(errorResponse.status()).isEqualTo(status);
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.errors()).isEqualTo(errors);
        assertThat(errorResponse.errors()).hasSize(2);
    }

    @Test
    void createErrorResponse_WithNullErrors_ShouldAcceptNullValue() {
        String message = "Test error message";
        int status = 404;

        ErrorResponse errorResponse = new ErrorResponse(message, status, null);

        assertThat(errorResponse.message()).isEqualTo(message);
        assertThat(errorResponse.status()).isEqualTo(status);
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.errors()).isNull();
    }

    @Test
    void createErrorResponse_WithoutErrors_ShouldUseOverloadedConstructor() {
        String message = "Test error message";
        int status = 404;

        ErrorResponse errorResponse = new ErrorResponse(message, status);

        assertThat(errorResponse.message()).isEqualTo(message);
        assertThat(errorResponse.status()).isEqualTo(status);
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.errors()).isNull();
    }

    @Test
    void createErrorResponse_WithEmptyErrors_ShouldAcceptEmptyMap() {
        String message = "Test error message";
        int status = 500;
        Map<String, List<String>> errors = new HashMap<>();

        ErrorResponse errorResponse = new ErrorResponse(message, status, errors);

        assertThat(errorResponse.message()).isEqualTo(message);
        assertThat(errorResponse.status()).isEqualTo(status);
        assertThat(errorResponse.timestamp()).isNotNull();
        assertThat(errorResponse.errors()).isEqualTo(errors);
        assertThat(errorResponse.errors()).isEmpty();
    }

    @Test
    void errorResponse_ShouldSupportEqualityComparison() {
        String message = "Test error";
        int status = 400;
        Map<String, List<String>> errors = Map.of("field", List.of("error"));

        // Timestamps will differ, so we can't compare records directly if we use the short constructor.
        // Let's use the canonical constructor for this test to control the timestamp.
        LocalDateTime timestamp = LocalDateTime.of(2023, 12, 25, 10, 30, 0);
        ErrorResponse errorResponse1 = new ErrorResponse(message, status, timestamp, errors);
        ErrorResponse errorResponse2 = new ErrorResponse(message, status, timestamp, errors);
        ErrorResponse errorResponse3 = new ErrorResponse("Different message", status, timestamp, errors);

        assertThat(errorResponse1).isEqualTo(errorResponse2);
        assertThat(errorResponse1).isNotEqualTo(errorResponse3);
        assertThat(errorResponse1.hashCode()).isEqualTo(errorResponse2.hashCode());
    }

    @Test
    void errorResponse_ShouldHaveMeaningfulToString() {
        String message = "Test error";
        int status = 400;
        Map<String, List<String>> errors = Map.of("field", List.of("error"));

        ErrorResponse errorResponse = new ErrorResponse(message, status, errors);

        String stringRepresentation = errorResponse.toString();

        assertThat(stringRepresentation).contains("Test error");
        assertThat(stringRepresentation).contains("400");
        assertThat(stringRepresentation).contains("field");
        assertThat(stringRepresentation).contains("error");
    }

    @Test
    void createErrorResponse_WithDifferentStatusCodes_ShouldStoreCorrectly() {
        ErrorResponse response400 = new ErrorResponse("Bad Request", 400);
        ErrorResponse response404 = new ErrorResponse("Not Found", 404);
        ErrorResponse response500 = new ErrorResponse("Internal Server Error", 500);

        assertThat(response400.status()).isEqualTo(400);
        assertThat(response404.status()).isEqualTo(404);
        assertThat(response500.status()).isEqualTo(500);
    }

    @Test
    void createErrorResponse_WithComplexErrorMap_ShouldStoreCorrectly() {
        String message = "Validation failed";
        int status = 400;
        Map<String, List<String>> errors = new HashMap<>();
        errors.put("name", List.of("Name is required"));
        errors.put("email", List.of("Email format is invalid"));
        errors.put("price", List.of("Price must be positive", "Price must be a number"));

        ErrorResponse errorResponse = new ErrorResponse(message, status, errors);

        assertThat(errorResponse.errors()).hasSize(3);
        assertThat(errorResponse.errors().get("name")).containsExactly("Name is required");
        assertThat(errorResponse.errors().get("email")).containsExactly("Email format is invalid");
        assertThat(errorResponse.errors().get("price")).containsExactly("Price must be positive", "Price must be a number");
    }
}