package com.products.infrastructure.rest.controller;

import com.products.application.ProductUseCase;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.infrastructure.dto.ProductPageResponseDTO;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.mapper.ProductMapper;
import com.products.infrastructure.rest.openapi.ProductApi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
public class ProductController implements ProductApi {

    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;

    public ProductController(final ProductUseCase productUseCase,
            final ProductMapper productMapper) {
        this.productUseCase = productUseCase;
        this.productMapper = productMapper;
    }

    @Override
    public ResponseEntity<ProductResponseDTO> createProduct(final ProductRequestDTO productRequestDTO) {
        final Product productRequest = productMapper.requestDtoToDomain(productRequestDTO);
        final Product createdProduct = productUseCase.createProduct(productRequest);
        final ProductResponseDTO response = productMapper.domainToResponseDTO(createdProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<ProductPageResponseDTO> getAllActiveProducts(
            final String cursor, final Integer limit, final String sortBy, final String sortDir,
            final String category, final String name, final Boolean active) {

        final PaginationQuery paginationQuery = productMapper.toPaginationQuery(cursor, limit, sortBy, sortDir);
        final ProductFilter filter = new ProductFilter(category, name, active);

        final PaginatedResult<Product> productResult = productUseCase.getAllActiveProducts(paginationQuery, filter);
        final ProductPageResponseDTO response = productMapper.toPageResponseDTO(productResult);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ProductResponseDTO> getProductById(final Long id) {
        final Product product = productUseCase.getActiveProductById(id);
        final ProductResponseDTO response = productMapper.domainToResponseDTO(product);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ProductResponseDTO> updateProduct(
            final Long id, final ProductRequestDTO productRequestDTO) {
        final Product productRequest = productMapper.requestDtoToDomain(productRequestDTO);
        final Product updatedProduct = productUseCase.updateProduct(id, productRequest);
        final ProductResponseDTO response = productMapper.domainToResponseDTO(updatedProduct);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteProduct(final Long id) {
        final Product product = productUseCase.getById(id);
        productUseCase.deactivateProduct(product);
        return ResponseEntity.noContent().build();
    }

}