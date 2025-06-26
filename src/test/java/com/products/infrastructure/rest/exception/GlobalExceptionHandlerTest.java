package com.products.infrastructure.rest.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.products.domain.exception.ProductNotFoundException;
import com.products.infrastructure.rest.exception.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @Mock
    private Path path;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException("Product not found");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleProductNotFoundException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Product not found");
        assertThat(body.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("product")).containsExactly("Product not found");
    }

    @Test
    void handleValidationExceptions() {
        FieldError fieldError = new FieldError("objectName", "field", "defaultMessage");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleValidationExceptions(methodArgumentNotValidException);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Validation failed");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("field")).containsExactly("defaultMessage");
    }

    @Test
    void handleConstraintViolationException() {
        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("field");
        when(constraintViolation.getMessage()).thenReturn("message");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(constraintViolation));

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolationException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Constraint validation failed");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("field")).containsExactly("message");
    }

    @Test
    void handleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("paramName", "String");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMissingServletRequestParameter(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Missing required parameter: paramName");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("paramName")).containsExactly("Parameter is required");
    }

    @Test
    void handleMethodArgumentTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "value", String.class, "name", null, new Exception());

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMethodArgumentTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Invalid parameter type");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("name").getFirst()).contains(
                "Invalid value 'value' for parameter 'name'. Expected type is 'String'.");
    }

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Illegal argument");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Illegal argument");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("argument")).containsExactly("Illegal argument");
    }

    @Test
    void handleHttpMessageNotReadable_withInvalidFormatException() {
        InvalidFormatException cause = new InvalidFormatException(null, "Invalid value", "value", Integer.class);
        JsonMappingException.Reference ref = new JsonMappingException.Reference(null, "fieldName");
        cause.prependPath(ref);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("msg", cause, null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Invalid value for field: fieldName");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("fieldName")).containsExactly("Invalid value 'value' for field 'fieldName'");
    }

    @Test
    void handleHttpMessageNotReadable_withoutCause() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "Invalid request body", null, null);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Invalid request body");
        assertThat(body.errors()).isNull(); // En este caso específico, errors puede ser null cuando no hay causa específica
    }

    @Test
    void handleHttpMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Unsupported media type");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMediaTypeNotSupported(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("Unsupported media type");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("contentType")).isNotNull();
        assertThat(body.errors().get("contentType").getFirst()).contains("Unsupported media type. Supported types:");
    }

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Generic error");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.message()).isEqualTo("An unexpected error occurred");
        assertThat(body.errors()).isNotNull();
        assertThat(body.errors().get("general")).containsExactly("An unexpected error occurred: Exception");
    }
}