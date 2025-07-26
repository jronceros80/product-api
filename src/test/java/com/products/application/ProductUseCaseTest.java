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
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductUseCase productUseCase;

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
    @DisplayName("Should return paginated active products when valid pagination query and filter are provided")
    void getAllActiveProducts_ValidPaginationAndFilter_ReturnsPaginatedProducts() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("ELECTRONICS", "laptop", true);

        List<Product> products = Arrays.asList(
                new Product(1L, "Laptop", BigDecimal.valueOf(1000), ProductCategory.ELECTRONICS, true),
                new Product(2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS, true));

        PaginatedResult<Product> expectedResult = new PaginatedResult<>(
                products, "2", null, false, false, 2, 10);

        when(productService.getAllActiveProducts(paginationQuery, filter))
                .thenReturn(expectedResult);

        PaginatedResult<Product> result = productUseCase.getAllActiveProducts(paginationQuery, filter);

        assertThat(result.content()).hasSize(2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.limit()).isEqualTo(10);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        verify(productService).getAllActiveProducts(paginationQuery, filter);
    }

    @Test
    @DisplayName("Should return empty list when no products match the criteria")
    void getAllActiveProducts_NoMatchingProducts_ReturnsEmptyList() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("CLOTHING", "jacket", true);

        PaginatedResult<Product> expectedResult = new PaginatedResult<>(
                Collections.emptyList(), null, null, false, false, 0, 10);

        when(productService.getAllActiveProducts(paginationQuery, filter))
                .thenReturn(expectedResult);

        PaginatedResult<Product> result = productUseCase.getAllActiveProducts(paginationQuery, filter);

        assertThat(result.content()).isEmpty();
        assertThat(result.size()).isEqualTo(0);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        verify(productService).getAllActiveProducts(paginationQuery, filter);
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
        Product productUpdate = new Product(
                1L,
                "Updated Product",
                BigDecimal.valueOf(79.99),
                ProductCategory.BOOKS);

        productUseCase.deactivateProduct(productUpdate);

        verify(productService).deactivateProduct(productUpdate);
    }
}