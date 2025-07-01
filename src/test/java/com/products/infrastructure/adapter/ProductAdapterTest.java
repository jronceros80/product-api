package com.products.infrastructure.adapter;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.infrastructure.mapper.ProductMapper;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.postgresql.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductAdapterTest {

    @Mock
    private ProductJpaRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductAdapter adapter;

    private ProductEntity testEntity;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testEntity = new ProductEntity("Test Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);
        testEntity.setId(1L);

        testProduct = new Product(1L, "Test Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);
    }

    @Test
    void save_ShouldReturnSavedProduct() {
        when(mapper.toEntity(any(Product.class))).thenReturn(testEntity);
        when(repository.save(any(ProductEntity.class))).thenReturn(testEntity);
        when(mapper.toDomain(any(ProductEntity.class))).thenReturn(testProduct);

        Product result = adapter.save(testProduct);

        assertThat(result).isEqualTo(testProduct);
        verify(mapper).toEntity(testProduct);
        verify(repository).save(testEntity);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    void findById_ShouldReturnProduct_WhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testProduct);

        Optional<Product> result = adapter.findById(1L);

        assertThat(result).isPresent().contains(testProduct);
        verify(repository).findById(1L);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    void findActiveById_ShouldReturnProduct_WhenActiveProductExists() {
        when(repository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(testEntity));
        when(mapper.toDomain(testEntity)).thenReturn(testProduct);

        Optional<Product> result = adapter.findActiveById(1L);

        assertThat(result).isPresent().contains(testProduct);
        verify(repository).findByIdAndActiveTrue(1L);
        verify(mapper).toDomain(testEntity);
    }

    @Test
    @DisplayName("Should return paginated products when findActiveProducts is called")
    void findActiveProducts_ShouldReturnPaginatedProducts() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("ELECTRONICS", "laptop", true);

        ProductEntity entity1 = createProductEntity(1L, "Laptop", BigDecimal.valueOf(1000),
                ProductCategory.ELECTRONICS);
        ProductEntity entity2 = createProductEntity(2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS);
        List<ProductEntity> entities = Arrays.asList(entity1, entity2);

        Product product1 = new Product(1L, "Laptop", BigDecimal.valueOf(1000), ProductCategory.ELECTRONICS, true);
        Product product2 = new Product(2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS, true);

        when(repository.findProductsAfterCursor(
                null, true, "ELECTRONICS", "laptop", 11))
                .thenReturn(entities);

        when(mapper.toDomain(entity1)).thenReturn(product1);
        when(mapper.toDomain(entity2)).thenReturn(product2);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.content()).hasSize(2);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.limit()).isEqualTo(10);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();
        assertThat(result.nextCursor()).isEqualTo("2");

        verify(repository).findProductsAfterCursor(null, true, "ELECTRONICS", "laptop", 11);
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
    }

    private ProductEntity createProductEntity(Long id, String name, BigDecimal price, ProductCategory category) {
        ProductEntity entity = new ProductEntity(name, price, category, true);
        entity.setId(id);
        return entity;
    }

    @Test
    void deactivateProduct_ShouldSetInactiveAndSave_WhenProductIsActive() {
        when(repository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(repository.save(any(ProductEntity.class))).thenReturn(testEntity);

        adapter.deactivateProduct(1L);

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(repository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().isActive()).isFalse();
    }

    @Test
    void deactivateProduct_ShouldThrowException_WhenProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adapter.deactivateProduct(1L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 1");
    }

    @Test
    @DisplayName("Should handle already inactive product gracefully")
    void deactivateProduct_ShouldLogMessage_WhenProductIsAlreadyInactive() {
        ProductEntity inactiveEntity = new ProductEntity("Test Product", new BigDecimal("99.99"),
                ProductCategory.ELECTRONICS, false);
        inactiveEntity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(inactiveEntity));

        adapter.deactivateProduct(1L);

        // Product should not be saved again since it's already inactive
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should handle multiple deactivation attempts gracefully")
    void deactivateProduct_ShouldLogMultipleDeactivationAttempts() {
        ProductEntity inactiveEntity = new ProductEntity("Test Product", new BigDecimal("99.99"),
                ProductCategory.ELECTRONICS, false);
        inactiveEntity.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(inactiveEntity));

        // First deactivation - should work (logs message)
        adapter.deactivateProduct(1L);

        // Second deactivation - should also work (just logs message)
        adapter.deactivateProduct(1L);

        // Verify that findById was called twice
        verify(repository, times(2)).findById(1L);
    }

    @Test
    @DisplayName("Should handle invalid cursor format")
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
    @DisplayName("Should handle empty cursor")
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
    @DisplayName("Should set hasPrevious when cursor is provided")
    void findActiveProducts_ShouldSetHasPreviousWhenCursorProvided() {
        PaginationQuery paginationQuery = new PaginationQuery("5", 10, "id", "asc");
        ProductFilter filter = new ProductFilter(null, null, true);

        ProductEntity entity = createProductEntity(6L, "Product", BigDecimal.valueOf(100), ProductCategory.ELECTRONICS);
        when(repository.findProductsAfterCursor(5L, true, null, null, 11))
                .thenReturn(List.of(entity));

        Product product = new Product(6L, "Product", BigDecimal.valueOf(100), ProductCategory.ELECTRONICS, true);
        when(mapper.toDomain(entity)).thenReturn(product);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.previousCursor()).isEqualTo("6");
    }

    @Test
    @DisplayName("Should handle hasNext when there are more results")
    void findActiveProducts_ShouldSetHasNextWhenMoreResults() {
        PaginationQuery paginationQuery = new PaginationQuery(null, 2, "id", "asc");
        ProductFilter filter = new ProductFilter(null, null, true);

        // Return 3 entities when limit is 2 (2 + 1 for hasNext check)
        ProductEntity entity1 = createProductEntity(1L, "Product1", BigDecimal.valueOf(100),
                ProductCategory.ELECTRONICS);
        ProductEntity entity2 = createProductEntity(2L, "Product2", BigDecimal.valueOf(200),
                ProductCategory.ELECTRONICS);
        ProductEntity entity3 = createProductEntity(3L, "Product3", BigDecimal.valueOf(300),
                ProductCategory.ELECTRONICS);

        when(repository.findProductsAfterCursor(null, true, null, null, 3))
                .thenReturn(List.of(entity1, entity2, entity3));

        Product product1 = new Product(1L, "Product1", BigDecimal.valueOf(100), ProductCategory.ELECTRONICS, true);
        Product product2 = new Product(2L, "Product2", BigDecimal.valueOf(200), ProductCategory.ELECTRONICS, true);

        when(mapper.toDomain(entity1)).thenReturn(product1);
        when(mapper.toDomain(entity2)).thenReturn(product2);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.content()).hasSize(2); // Should only return 2, not 3
        assertThat(result.nextCursor()).isEqualTo("2");
    }
}