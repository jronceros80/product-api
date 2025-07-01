package com.products.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductPageResponseDTOTest {

    @Test
    void shouldCreatePageInfoWithCursorPagination() {
        PageInfo pageInfo = new PageInfo(10, 20, true, false, "nextCursor123", null);

        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.limit()).isEqualTo(20);
        assertThat(pageInfo.hasNext()).isTrue();
        assertThat(pageInfo.hasPrevious()).isFalse();
        assertThat(pageInfo.nextCursor()).isEqualTo("nextCursor123");
        assertThat(pageInfo.previousCursor()).isNull();
    }

    @Test
    void shouldCreatePageInfoWithoutCursors() {
        PageInfo pageInfo = new PageInfo(5, 10, false, true, null, "prevCursor456");

        assertThat(pageInfo.size()).isEqualTo(5);
        assertThat(pageInfo.limit()).isEqualTo(10);
        assertThat(pageInfo.hasNext()).isFalse();
        assertThat(pageInfo.hasPrevious()).isTrue();
        assertThat(pageInfo.nextCursor()).isNull();
        assertThat(pageInfo.previousCursor()).isEqualTo("prevCursor456");
    }

    @Test
    void shouldCreateProductPageResponseDTO() {
        PageInfo pageInfo = new PageInfo(1, 20, false, false, null, null);
        List<ProductResponseDTO> products = Collections.singletonList(
                new ProductResponseDTO(1L, "Test Product", BigDecimal.valueOf(100),
                        com.products.domain.model.ProductCategory.ELECTRONICS, true));

        ProductPageResponseDTO responseDTO = new ProductPageResponseDTO(
                products, null, null, false, false, 1, 20, pageInfo);

        assertThat(responseDTO.content()).hasSize(1);
        assertThat(responseDTO.pageInfo()).isEqualTo(pageInfo);
        assertThat(responseDTO.content().getFirst().name()).isEqualTo("Test Product");
        assertThat(responseDTO.hasNext()).isFalse();
        assertThat(responseDTO.hasPrevious()).isFalse();
        assertThat(responseDTO.size()).isEqualTo(1);
        assertThat(responseDTO.limit()).isEqualTo(20);
    }
}