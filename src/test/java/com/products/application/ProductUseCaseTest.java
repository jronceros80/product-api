package com.products.application;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.domain.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductUseCase productUseCase;

    private Product sampleProduct;
    private ProductFilter defaultFilter;
    private PaginationQuery defaultPaginationQuery;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        defaultFilter = new ProductFilter(null, null, null);
        defaultPaginationQuery = new PaginationQuery(0, 10);
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct_WhenValidProduct() {
        Product productToCreate = new Product(
                "New Product",
                BigDecimal.valueOf(49.99),
                ProductCategory.BOOKS);

        when(productService.createProduct(productToCreate)).thenReturn(sampleProduct);

        Product result = productUseCase.createProduct(productToCreate);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productService).createProduct(productToCreate);
    }

    @Test
    void getAllActiveProducts_ShouldReturnProductPage_WhenValidRequest() {
        PaginatedResult<Product> domainResult = new PaginatedResult<>(
                Collections.singletonList(sampleProduct), 1L, 1, 0, 10);

        when(productService.getAllActiveProducts(eq(defaultPaginationQuery), eq(defaultFilter)))
                .thenReturn(domainResult);

        PaginatedResult<Product> result = productUseCase.getAllActiveProducts(defaultPaginationQuery, defaultFilter);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isEqualTo(sampleProduct);
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(productService).getAllActiveProducts(eq(defaultPaginationQuery), eq(defaultFilter));
    }

    @Test
    void getAllActiveProducts_ShouldReturnFilteredProducts_WhenCategoryProvided() {
        ProductFilter customFilter = new ProductFilter("ELECTRONICS", null, null);
        PaginatedResult<Product> domainResult = new PaginatedResult<>(
                Collections.singletonList(sampleProduct), 1L, 1, 0, 10);

        when(productService.getAllActiveProducts(eq(defaultPaginationQuery), eq(customFilter)))
                .thenReturn(domainResult);

        PaginatedResult<Product> result = productUseCase.getAllActiveProducts(defaultPaginationQuery, customFilter);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isEqualTo(sampleProduct);
        verify(productService).getAllActiveProducts(eq(defaultPaginationQuery), eq(customFilter));
    }

    @Test
    void getActiveProductById_ShouldReturnProduct_WhenValidId() {
        Long productId = 1L;
        when(productService.getActiveProductById(productId)).thenReturn(sampleProduct);

        Product result = productUseCase.getActiveProductById(productId);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productService).getActiveProductById(productId);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenValidIdAndProduct() {
        Long productId = 1L;
        Product productUpdate = new Product(
                "Updated Product",
                BigDecimal.valueOf(79.99),
                ProductCategory.BOOKS);

        when(productService.updateProduct(productId, productUpdate)).thenReturn(sampleProduct);

        Product result = productUseCase.updateProduct(productId, productUpdate);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productService).updateProduct(productId, productUpdate);
    }

    @Test
    void deactivateProduct_ShouldCallService_WhenValidId() {
        Long productId = 1L;

        productUseCase.deactivateProduct(productId);

        verify(productService).deactivateProduct(productId);
    }
}