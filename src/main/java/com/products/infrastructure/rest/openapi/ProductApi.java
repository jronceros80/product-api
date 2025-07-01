package com.products.infrastructure.rest.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.products.infrastructure.dto.ProductPageResponseDTO;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;

@Tag(name = "Products", description = "Product management API")
public interface ProductApi {

        @PostMapping("/products")
        @Operation(summary = "Create a new product", description = "Creates a new product with the provided information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Product created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        ResponseEntity<ProductResponseDTO> createProduct(
                        @Valid @RequestBody ProductRequestDTO request);

        @GetMapping("/products")
        @Operation(summary = "Get all active products", description = "Retrieves all active products with cursor-based pagination for better performance and consistency")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        ResponseEntity<ProductPageResponseDTO> getAllActiveProducts(
                        @Parameter(description = "Cursor for pagination. Use the nextCursor from previous response to get next page. "
                                        +
                                        "Leave empty for first page.") @RequestParam(value = "cursor", required = false) String cursor,

                        @Parameter(description = "Number of items per page (1-100, default: 20)") @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,

                        @Parameter(description = "Sort field (default: id)") @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy,

                        @Parameter(description = "Sort direction: asc or desc (default: asc)") @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,

                        @Parameter(description = "Filter by category (optional)") @RequestParam(value = "category", required = false) String category,

                        @Parameter(description = "Filter by name (optional)") @RequestParam(value = "name", required = false) String name,

                        @Parameter(description = "Filter by active status (optional, defaults to true)") @RequestParam(value = "active", required = false) Boolean active);

        @GetMapping("/products/{id}")
        @Operation(summary = "Get product by ID", description = "Retrieves an active product by its ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found"),
                        @ApiResponse(responseCode = "404", description = "Product not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        ResponseEntity<ProductResponseDTO> getProductById(
                        @Parameter(description = "Product ID") @PathVariable Long id);

        @PutMapping("/products/{id}")
        @Operation(summary = "Update product", description = "Updates an existing product with the provided information")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "404", description = "Product not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        ResponseEntity<ProductResponseDTO> updateProduct(
                        @Parameter(description = "Product ID") @PathVariable Long id,
                        @Valid @RequestBody ProductRequestDTO request);

        @DeleteMapping("/products/{id}")
        @Operation(summary = "Deactivate product", description = "Deactivates a product (soft delete)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Product deactivated successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        ResponseEntity<Void> deleteProduct(
                        @Parameter(description = "Product ID") @PathVariable Long id);
}