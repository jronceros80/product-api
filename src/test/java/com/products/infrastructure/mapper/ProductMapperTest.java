package com.products.infrastructure.mapper;

import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.postgresql.entity.ProductEntity;

import com.products.domain.model.PaginationQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
    }

    @Test
    void toDomainModel_ShouldConvertRequestDTO_ToProduct() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        Product result = mapper.toDomain(requestDTO);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void toDomainModel_ShouldConvertEntity_ToProduct() {
        ProductEntity entity = new ProductEntity("Test Product", BigDecimal.valueOf(99.99), ProductCategory.ELECTRONICS,
                true);
        entity.setId(1L);

        Product result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void toResponseDTO_ShouldConvertProduct_ToResponseDTO() {
        Product product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        ProductResponseDTO result = mapper.toResponseDTO(product);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void toEntity_ShouldConvertProduct_ToEntity() {
        Product product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        ProductEntity result = mapper.toEntity(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals(ProductCategory.ELECTRONICS, result.getCategory());
        assertTrue(result.isActive());
    }

    @Test
    void toDomain_ShouldHandleInactiveProducts() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Inactive Product",
                BigDecimal.valueOf(199.99),
                ProductCategory.BOOKS,
                false);

        Product result = mapper.toDomain(requestDTO);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Inactive Product", result.name());
        assertEquals(BigDecimal.valueOf(199.99), result.price());
        assertEquals(ProductCategory.BOOKS, result.category());
        assertFalse(result.active());
    }

    @Test
    void toDomain_ShouldHandleNullActiveField() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Product with null active",
                BigDecimal.valueOf(299.99),
                ProductCategory.CLOTHING,
                null);

        Product result = mapper.toDomain(requestDTO);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Product with null active", result.name());
        assertEquals(BigDecimal.valueOf(299.99), result.price());
        assertEquals(ProductCategory.CLOTHING, result.category());
        assertTrue(result.active());
    }

    @Test
    void roundTripConversion_ShouldPreserveData() {
        Product originalProduct = new Product(
                1L,
                "Round Trip Product",
                BigDecimal.valueOf(599.99),
                ProductCategory.CLOTHING,
                true);

        ProductResponseDTO responseDTO = mapper.toResponseDTO(originalProduct);
        ProductEntity entity = mapper.toEntity(originalProduct);
        Product reconvertedProduct = mapper.toDomain(entity);

        assertEquals(originalProduct.id(), reconvertedProduct.id());
        assertEquals(originalProduct.name(), reconvertedProduct.name());
        assertEquals(originalProduct.price(), reconvertedProduct.price());
        assertEquals(originalProduct.category(), reconvertedProduct.category());
        assertEquals(originalProduct.active(), reconvertedProduct.active());

        assertEquals(originalProduct.id(), responseDTO.id());
        assertEquals(originalProduct.name(), responseDTO.name());
        assertEquals(originalProduct.price(), responseDTO.price());
        assertEquals(originalProduct.category(), responseDTO.category());
        assertEquals(originalProduct.active(), responseDTO.active());
    }

    @Test
    void toPaginationQuery_ShouldUseDefaults_WhenNoSortingProvided() {
        Pageable pageable = PageRequest.of(0, 10);

        PaginationQuery result = mapper.toPaginationQuery(pageable);

        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.sortBy()).isEqualTo("id");
        assertThat(result.sortDir()).isEqualTo("asc");
    }

    @Test
    void toPaginationQuery_ShouldExtractSorting_WhenSortingProvided() {
        Sort sort = Sort.by(Sort.Direction.DESC, "name");
        Pageable pageable = PageRequest.of(1, 20, sort);

        PaginationQuery result = mapper.toPaginationQuery(pageable);

        assertThat(result.pageNumber()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(20);
        assertThat(result.sortBy()).isEqualTo("name");
        assertThat(result.sortDir()).isEqualTo("desc");
    }

    @Test
    void toPaginationQuery_ShouldHandleAscendingSort() {
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        Pageable pageable = PageRequest.of(2, 5, sort);

        PaginationQuery result = mapper.toPaginationQuery(pageable);

        assertThat(result.pageNumber()).isEqualTo(2);
        assertThat(result.pageSize()).isEqualTo(5);
        assertThat(result.sortBy()).isEqualTo("price");
        assertThat(result.sortDir()).isEqualTo("asc");
    }
}