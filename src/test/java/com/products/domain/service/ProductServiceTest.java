package com.products.domain.service;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductKafkaPort;
import com.products.domain.port.ProductMongoPort;
import com.products.domain.port.ProductPostgresPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.DisplayName;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPostgresPort productPostgresPort;

    @Mock
    private ProductMongoPort productMongoPort;

    @Mock
    private ProductKafkaPort productEventPort;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);
    }

    @Test
    void createProduct_ShouldReturnSavedProduct_WhenValidProduct() {
        Product productToSave = new Product(
                "New Product",
                BigDecimal.valueOf(49.99),
                ProductCategory.BOOKS);

        when(productPostgresPort.save(productToSave)).thenReturn(sampleProduct);

        Product result = productService.createProduct(productToSave);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productPostgresPort).save(productToSave);
    }

    @Test
    @DisplayName("Should return paginated active products when valid pagination query and filter are provided")
    void getAllActiveProducts_ValidPaginationAndFilter_ReturnsPaginatedProducts() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("ELECTRONICS", "laptop", true);

        List<Product> products = Arrays.asList(
                new Product(1L, "Laptop", BigDecimal.valueOf(1000), ProductCategory.ELECTRONICS, true),
                new Product(2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS, true));

        PaginatedResult<Product> expectedResult = new PaginatedResult<>(
                products, "2", null, false, false, 2, 10);

        when(productMongoPort.findActiveProducts(paginationQuery, filter))
                .thenReturn(expectedResult);

        PaginatedResult<Product> result = productService.getAllActiveProducts(paginationQuery, filter);

        assertThat(result.content()).hasSize(2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.limit()).isEqualTo(10);
        verify(productMongoPort).findActiveProducts(paginationQuery, filter);
    }

    @Test
    @DisplayName("Should return empty list when no products match the criteria")
    void getAllActiveProducts_NoMatchingProducts_ReturnsEmptyList() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("CLOTHING", "jacket", true);

        PaginatedResult<Product> expectedResult = new PaginatedResult<>(
                Collections.emptyList(), null, null, false, false, 0, 10);

        when(productMongoPort.findActiveProducts(paginationQuery, filter))
                .thenReturn(expectedResult);

        PaginatedResult<Product> result = productService.getAllActiveProducts(paginationQuery, filter);

        assertThat(result.content()).isEmpty();
        assertThat(result.size()).isEqualTo(0);
        verify(productMongoPort).findActiveProducts(paginationQuery, filter);
    }

    @Test
    void getActiveProductById_ShouldReturnProduct_WhenProductExists() {
        Long productId = 1L;
        when(productMongoPort.findActiveById(productId)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getActiveProductById(productId);

        assertThat(result).isEqualTo(sampleProduct);
        verify(productMongoPort).findActiveById(productId);
    }

    @Test
    void getActiveProductById_ShouldThrowException_WhenProductNotFound() {
        Long productId = 999L;
        when(productMongoPort.findActiveById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getActiveProductById(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Active product not found with id: " + productId);

        verify(productMongoPort).findActiveById(productId);
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

        when(productMongoPort.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productPostgresPort.save(expectedUpdatedProduct)).thenReturn(expectedUpdatedProduct);

        Product result = productService.updateProduct(productId, productUpdate);

        assertThat(result).isEqualTo(expectedUpdatedProduct);
        verify(productMongoPort).findById(productId);
        verify(productPostgresPort).save(expectedUpdatedProduct);
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductNotFound() {
        Long productId = 999L;
        Product productUpdate = new Product(
                "Updated Product",
                BigDecimal.valueOf(75.00),
                ProductCategory.BOOKS);

        when(productMongoPort.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(productId, productUpdate))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: " + productId);

        verify(productMongoPort).findById(productId);
    }

    @Test
    void deactivateProduct_ShouldCallRepository_WhenValidId() {
        Product productUpdate = new Product(
                1L,
                "Updated Product",
                BigDecimal.valueOf(79.99),
                ProductCategory.BOOKS);

        productService.deactivateProduct(productUpdate);

        verify(productPostgresPort).deactivateProduct(productUpdate.id());
    }
}