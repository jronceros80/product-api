package com.products.infrastructure.dto;

import com.products.domain.model.ProductCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Product name is required") @Size(min = 2, max = 100,
                message = "Product name must be between 2 and 100 characters") String name,

        @NotNull(message = "Price is required") @DecimalMin(value = "0.01",
                message = "Price must be greater than 0") @Digits(integer = 8, fraction = 2,
                message = "Price must have at most 8 integer digits and 2 decimal places") BigDecimal price,

        @NotNull(message = "Category is required") ProductCategory category, Boolean active) {

    public ProductRequestDTO {
        active = active != null ? active : true;
    }
}