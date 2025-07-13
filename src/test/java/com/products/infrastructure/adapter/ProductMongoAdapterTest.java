package com.products.infrastructure.adapter;

import com.products.domain.model.*;
import com.products.infrastructure.mapper.ProductMapper;
import com.products.infrastructure.mongo.document.ProductDocument;
import com.products.infrastructure.mongo.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductMongoAdapterTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductMongoAdapter adapter;

    private ProductDocument testDocument;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testDocument = new ProductDocument(1L, "Test Product", new BigDecimal("99.99"), "Electronics", true);
        testProduct = new Product(1L, "Test Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);
    }


    @Test
    void findById_ShouldReturnProduct_WhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(mapper.documentToDomain(testDocument)).thenReturn(testProduct);

        Optional<Product> result = adapter.findById(1L);

        assertThat(result).isPresent().contains(testProduct);
        verify(repository).findById(1L);
        verify(mapper).documentToDomain(testDocument);
    }

    @Test
    void findActiveById_ShouldReturnProduct_WhenActiveProductExists() {
        when(repository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(testDocument));
        when(mapper.documentToDomain(testDocument)).thenReturn(testProduct);

        Optional<Product> result = adapter.findActiveById(1L);

        assertThat(result).isPresent().contains(testProduct);
        verify(repository).findByIdAndActiveTrue(1L);
        verify(mapper).documentToDomain(testDocument);
    }

    @Test
    void findActiveProducts_ShouldReturnPaginatedProducts() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("ELECTRONICS", "laptop", true);

        ProductDocument document1 = createProductDocument(1L, "Laptop", BigDecimal.valueOf(1000),
                ProductCategory.ELECTRONICS);

        ProductDocument document2 = createProductDocument(
                2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS);

        List<ProductDocument> entities = Arrays.asList(document1, document2);

        Product product1 = new Product(1L, "Laptop", BigDecimal.valueOf(1000), ProductCategory.ELECTRONICS, true);
        Product product2 = new Product(2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS, true);

        when(repository.findProductsAfterCursor(
                null, true, "ELECTRONICS", "laptop", 11))
                .thenReturn(entities);

        when(mapper.documentToDomain(document1)).thenReturn(product1);
        when(mapper.documentToDomain(document2)).thenReturn(product2);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.content()).hasSize(2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.limit()).isEqualTo(10);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.nextCursor()).isEqualTo("2");

        verify(repository).findProductsAfterCursor(null, true, "ELECTRONICS", "laptop", 11);
        verify(mapper).documentToDomain(document1);
        verify(mapper).documentToDomain(document2);
    }

    @Test
    void findActiveProducts_ShouldHandleInvalidCursor() {
        PaginationQuery paginationQuery = new PaginationQuery("invalid-cursor", 10, "id", "asc");
        ProductFilter filter = new ProductFilter(null, null, true);

        when(repository.findProductsAfterCursor(null, true, null, null, 11))
                .thenReturn(List.of());

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.content()).isEmpty();
        verify(repository).findProductsAfterCursor(null, true, null, null, 11);
    }

    @Test
    void findActiveProducts_ShouldHandleEmptyCursor() {
        PaginationQuery paginationQuery = new PaginationQuery("   ", 10, "id", "asc");
        ProductFilter filter = new ProductFilter(null, null, true);

        when(repository.findProductsAfterCursor(null, true, null, null, 11))
                .thenReturn(List.of());

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.content()).isEmpty();
        verify(repository).findProductsAfterCursor(null, true, null, null, 11);
    }

    @Test
    void findActiveProducts_ShouldSetHasPreviousWhenCursorProvided() {
        PaginationQuery paginationQuery = new PaginationQuery("5", 10, "id", "asc");
        ProductFilter filter = new ProductFilter(null, null, true);

        ProductDocument document = createProductDocument(6L, "Product", BigDecimal.valueOf(100), ProductCategory.ELECTRONICS);
        when(repository.findProductsAfterCursor(5L, true, null, null, 11))
                .thenReturn(List.of(document));

        Product product = new Product(6L, "Product", BigDecimal.valueOf(100), ProductCategory.ELECTRONICS, true);
        when(mapper.documentToDomain(document)).thenReturn(product);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.previousCursor()).isEqualTo("6");
    }

    @Test
    void findActiveProducts_ShouldSetHasNextWhenMoreResults() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 2, "id", "asc");
        ProductFilter filter = new ProductFilter(null, null, true);

        // Return 3 entities when limit is 2 (2 + 1 for hasNext check)
        ProductDocument document1 = createProductDocument(1L, "Product1", BigDecimal.valueOf(100),
                ProductCategory.ELECTRONICS);
        ProductDocument document2 = createProductDocument(2L, "Product2", BigDecimal.valueOf(200),
                ProductCategory.ELECTRONICS);
        ProductDocument document3 = createProductDocument(3L, "Product3", BigDecimal.valueOf(300),
                ProductCategory.ELECTRONICS);

        when(repository.findProductsAfterCursor(null, true, null, null, 3))
                .thenReturn(List.of(document1, document2, document3));

        Product product1 = new Product(1L, "Product1", BigDecimal.valueOf(100), ProductCategory.ELECTRONICS, true);
        Product product2 = new Product(2L, "Product2", BigDecimal.valueOf(200), ProductCategory.ELECTRONICS, true);

        when(mapper.documentToDomain(document1)).thenReturn(product1);
        when(mapper.documentToDomain(document2)).thenReturn(product2);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.content()).hasSize(2); // Should only return 2, not 3
        assertThat(result.nextCursor()).isEqualTo("2");
    }

    private ProductDocument createProductDocument(Long id, String name, BigDecimal price, ProductCategory category) {
        return new ProductDocument(id, name, price, category.name(), true);
    }

}