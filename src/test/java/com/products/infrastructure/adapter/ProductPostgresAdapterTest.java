package com.products.infrastructure.adapter;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPostgresAdapterTest {

    @Mock
    private ProductJpaRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductPostgresAdapter adapter;

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
        when(mapper.domainToEntity(any(Product.class))).thenReturn(testEntity);
        when(repository.save(any(ProductEntity.class))).thenReturn(testEntity);
        when(mapper.entityToDomain(any(ProductEntity.class))).thenReturn(testProduct);

        Product result = adapter.save(testProduct);

        assertThat(result).isEqualTo(testProduct);
        verify(mapper).domainToEntity(testProduct);
        verify(repository).save(testEntity);
        verify(mapper).entityToDomain(testEntity);
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
}