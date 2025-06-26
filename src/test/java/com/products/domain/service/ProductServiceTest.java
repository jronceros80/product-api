package com.products.domain.service;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPersistencePort productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;
    private PaginationQuery defaultPaginationQuery;
    private ProductFilter defaultFilter;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        defaultPaginationQuery = new PaginationQuery(0, 10);
        defaultFilter = new ProductFilter(null, null, null);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct_WhenValidProduct() {
        Product productToSave = new Product(
                "New Product",
                BigDecimal.valueOf(49.99),
                ProductCategory.BOOKS);

        when(productRepository.save(productToSave)).thenReturn(sampleProduct);

        Product result = productService.createProduct(productToSave);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productRepository).save(productToSave);
    }

    @Test
    void getAllActiveProducts_ShouldReturnProductPage_WhenProductsExist() {
        PaginatedResult<Product> productResult = new PaginatedResult<>(Collections.singletonList(sampleProduct), 1L, 1, 0, 10);
        when(productRepository.findActiveProducts(eq(defaultPaginationQuery), eq(defaultFilter))).thenReturn(productResult);

        PaginatedResult<Product> result = productService.getAllActiveProducts(defaultPaginationQuery, defaultFilter);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isEqualTo(sampleProduct);
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(productRepository).findActiveProducts(eq(defaultPaginationQuery), eq(defaultFilter));
    }

    @Test
    void getAllActiveProducts_ShouldReturnFilteredProducts_WhenCategoryProvided() {
        ProductFilter customFilter = new ProductFilter("ELECTRONICS", null, null);
        PaginatedResult<Product> productResult = new PaginatedResult<>(Collections.singletonList(sampleProduct), 1L, 1, 0, 10);
        when(productRepository.findActiveProducts(eq(defaultPaginationQuery), eq(customFilter))).thenReturn(productResult);

        PaginatedResult<Product> result = productService.getAllActiveProducts(defaultPaginationQuery, customFilter);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isEqualTo(sampleProduct);
        verify(productRepository).findActiveProducts(eq(defaultPaginationQuery), eq(customFilter));
    }

    @Test
    void getActiveProductById_ShouldReturnProduct_WhenProductExists() {
        Long productId = 1L;
        when(productRepository.findActiveById(productId)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getActiveProductById(productId);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productRepository).findActiveById(productId);
    }

    @Test
    void getActiveProductById_ShouldThrowException_WhenProductNotFound() {
        Long productId = 999L;
        when(productRepository.findActiveById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getActiveProductById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Active product not found with id: " + productId);

        verify(productRepository).findActiveById(productId);
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenProductExists() {
        Long productId = 1L;
        Product existingProduct = new Product(
                productId,
                "Existing Product",
                BigDecimal.valueOf(50.00),
                ProductCategory.ELECTRONICS,
                true);

        Product productUpdate = new Product(
                "Updated Product",
                BigDecimal.valueOf(75.00),
                ProductCategory.BOOKS);

        Product expectedUpdatedProduct = new Product(
                productId,
                "Updated Product",
                BigDecimal.valueOf(75.00),
                ProductCategory.BOOKS,
                true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(expectedUpdatedProduct)).thenReturn(expectedUpdatedProduct);

        Product result = productService.updateProduct(productId, productUpdate);

        assertThat(result).isEqualTo(expectedUpdatedProduct);
        verify(productRepository).findById(productId);
        verify(productRepository).save(expectedUpdatedProduct);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        Long productId = 999L;
        Product productUpdate = new Product(
                "Updated Product",
                BigDecimal.valueOf(75.00),
                ProductCategory.BOOKS);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(productId, productUpdate))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: " + productId);

        verify(productRepository).findById(productId);
    }

    @Test
    void deactivateProduct_ShouldCallRepository_WhenValidId() {
        Long productId = 1L;

        productService.deactivateProduct(productId);

        verify(productRepository).deactivateProduct(productId);
    }
}