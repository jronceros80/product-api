package com.products.infrastructure.mapper;

import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.kafka.avro.generated.ProductEvent;
import com.products.infrastructure.mongo.document.ProductDocument;

import com.products.domain.model.PaginationQuery;

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
    void toDomainModel_ShouldConvertRequestDTO_requestDtoToProduct() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        Product result = mapper.requestDtoToDomain(requestDTO);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void toDomainModel_ShouldConvertEntity_requestDtoToProduct() {
        ProductEntity entity = new ProductEntity("Test Product", BigDecimal.valueOf(99.99), ProductCategory.ELECTRONICS,
                true);
        entity.setId(1L);

        Product result = mapper.entityToDomain(entity);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void toResponseDTO_ShouldConvertProduct_domainToResponseDTO() {
        Product product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        ProductResponseDTO result = mapper.domainToResponseDTO(product);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void toEntity_ShouldConvertProduct_domainToEntity() {
        Product product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        ProductEntity result = mapper.domainToEntity(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals(ProductCategory.ELECTRONICS, result.getCategory());
        assertTrue(result.isActive());
    }

    @Test
    void requestDtoToDomain_ShouldHandleInactiveProducts() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Inactive Product",
                BigDecimal.valueOf(199.99),
                ProductCategory.BOOKS,
                false);

        Product result = mapper.requestDtoToDomain(requestDTO);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("Inactive Product", result.name());
        assertEquals(BigDecimal.valueOf(199.99), result.price());
        assertEquals(ProductCategory.BOOKS, result.category());
        assertFalse(result.active());
    }

    @Test
    void requestDtoToDomain_ShouldHandleNullActiveField() {
        ProductRequestDTO requestDTO = new ProductRequestDTO(
                "Product with null active",
                BigDecimal.valueOf(299.99),
                ProductCategory.CLOTHING,
                null);

        Product result = mapper.requestDtoToDomain(requestDTO);

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

        ProductResponseDTO responseDTO = mapper.domainToResponseDTO(originalProduct);
        ProductEntity entity = mapper.domainToEntity(originalProduct);
        Product reconvertedProduct = mapper.entityToDomain(entity);

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
    void toPaginationQuery_ValidCursorParams_ReturnsPaginationQuery() {
        String cursor = "123";
        Integer limit = 20;
        String sortBy = "name";
        String sortDir = "desc";

        PaginationQuery result = mapper.toPaginationQuery(cursor, limit, sortBy, sortDir);

        assertThat(result.cursor()).isEqualTo("123");
        assertThat(result.limit()).isEqualTo(20);
        assertThat(result.sortBy()).isEqualTo("name");
        assertThat(result.sortDir()).isEqualTo("desc");
    }

    @Test
    void toPaginationQuery_NullParams_ReturnsDefaultPaginationQuery() {
        PaginationQuery result = mapper.toPaginationQuery(null, null, null, null);

        assertThat(result.cursor()).isNull();
        assertThat(result.limit()).isEqualTo(20);
        assertThat(result.sortBy()).isEqualTo("id");
        assertThat(result.sortDir()).isEqualTo("asc");
    }

    @Test
    void toPaginationQuery_InvalidLimit_UsesDefaultLimit() {
        PaginationQuery result1 = mapper.toPaginationQuery(null, 200, "id", "asc");

        PaginationQuery result2 = mapper.toPaginationQuery(null, 0, "id", "asc");

        assertThat(result1.limit()).isEqualTo(20);
        assertThat(result2.limit()).isEqualTo(20);
    }

    @Test
    void toPaginationQuery_BlankSortParams_ReturnsDefaultSortParams() {
        PaginationQuery result = mapper.toPaginationQuery("123", 15, "   ", "   ");

        assertThat(result.cursor()).isEqualTo("123");
        assertThat(result.limit()).isEqualTo(15);
        assertThat(result.sortBy()).isEqualTo("id");
        assertThat(result.sortDir()).isEqualTo("asc");
    }

    @Test
    void toPaginationQuery_EmptySortParams_ReturnsDefaultSortParams() {
        PaginationQuery result = mapper.toPaginationQuery("456", 25, "", "");

        assertThat(result.cursor()).isEqualTo("456");
        assertThat(result.limit()).isEqualTo(25);
        assertThat(result.sortBy()).isEqualTo("id");
        assertThat(result.sortDir()).isEqualTo("asc");
    }

    @Test
    void toPaginationQuery_MixedParams_ReturnsCorrectPaginationQuery() {
        PaginationQuery result = mapper.toPaginationQuery("789", null, "price", null);

        assertThat(result.cursor()).isEqualTo("789");
        assertThat(result.limit()).isEqualTo(20);
        assertThat(result.sortBy()).isEqualTo("price");
        assertThat(result.sortDir()).isEqualTo("asc");
    }

    @Test
    void domainToAvro_ShouldConvertProductToAvro() {
        Product product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        ProductEvent result = mapper.domainToAvro(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals("ELECTRONICS", result.getCategory());
        assertTrue(result.getActive());
    }

    @Test
    void avroToDomain_ShouldConvertAvroToProduct() {
        ProductEvent avroEvent = ProductEvent.newBuilder()
                .setId(1L)
                .setName("Test Product")
                .setPrice(BigDecimal.valueOf(99.99))
                .setCategory("ELECTRONICS")
                .setActive(true)
                .build();

        Product result = mapper.avroToDomain(avroEvent);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void domainToDocument_ShouldConvertProductToDocument() {
        Product product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);

        ProductDocument result = mapper.domainToDocument(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertEquals("ELECTRONICS", result.getCategory());
        assertTrue(result.getActive());
    }

    @Test
    void documentToDomain_ShouldConvertDocumentToProduct() {
        ProductDocument document = new ProductDocument(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                "ELECTRONICS",
                true);

        Product result = mapper.documentToDomain(document);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(BigDecimal.valueOf(99.99), result.price());
        assertEquals(ProductCategory.ELECTRONICS, result.category());
        assertTrue(result.active());
    }

    @Test
    void avroToDomainToDcoument_RoundTripConversion_ShouldPreserveData() {
        ProductEvent originalEvent = ProductEvent.newBuilder()
                .setId(1L)
                .setName("Round Trip Product")
                .setPrice(BigDecimal.valueOf(599.99))
                .setCategory("CLOTHING")
                .setActive(true)
                .build();

        Product product = mapper.avroToDomain(originalEvent);
        ProductDocument document = mapper.domainToDocument(product);
        Product reconvertedProduct = mapper.documentToDomain(document);
        ProductEvent reconvertedEvent = mapper.domainToAvro(reconvertedProduct);

        assertEquals(originalEvent.getId(), reconvertedEvent.getId());
        assertEquals(originalEvent.getName(), reconvertedEvent.getName());
        assertEquals(originalEvent.getPrice(), reconvertedEvent.getPrice());
        assertEquals(originalEvent.getCategory(), reconvertedEvent.getCategory());
        assertEquals(originalEvent.getActive(), reconvertedEvent.getActive());
    }
}