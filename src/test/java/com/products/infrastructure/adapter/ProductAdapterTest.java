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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    void findActiveProducts_ShouldReturnPaginatedResult() {
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "id", "asc");
        ProductFilter filter = new ProductFilter("ELECTRONICS", "Test", true);

        Pageable expectedPageable = PageRequest.of(0, 10, 
            org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.ASC, "id"));
        
        Page<ProductEntity> entityPage = new PageImpl<>(List.of(testEntity), expectedPageable, 1);

        when(mapper.toPageable(paginationQuery)).thenReturn(expectedPageable);
        when(repository.findProductsWithFilters(
                any(Boolean.class),
                any(ProductCategory.class),
                any(String.class),
                any(Pageable.class)
        )).thenReturn(entityPage);
        when(mapper.toDomain(testEntity)).thenReturn(testProduct);

        PaginatedResult<Product> result = adapter.findActiveProducts(paginationQuery, filter);

        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst()).isEqualTo(testProduct);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalPages()).isEqualTo(1);

        verify(mapper).toPageable(paginationQuery);
        verify(repository).findProductsWithFilters(
                any(Boolean.class),
                any(ProductCategory.class),
                any(String.class),
                any(Pageable.class)
        );
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
}