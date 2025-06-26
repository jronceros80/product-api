package com.products.infrastructure.dto;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProductPageResponseDTOTest {

    @Test
    void testPageInfo() {
        PageInfo pageInfo = new PageInfo(10, 0, 100, 10);

        assertEquals(10, pageInfo.getSize());
        assertEquals(100, pageInfo.getTotalElements());
        assertEquals(10, pageInfo.getTotalPages());
        assertEquals(0, pageInfo.getNumber());
        assertTrue(pageInfo.isFirst());
        assertFalse(pageInfo.isLast());
        assertTrue(pageInfo.isHasNext());
        assertFalse(pageInfo.isHasPrevious());
    }

    @Test
    void testPageInfo_LastPage() {
        PageInfo pageInfo = new PageInfo(20, 9, 200, 10);

        assertEquals(20, pageInfo.getSize());
        assertEquals(200, pageInfo.getTotalElements());
        assertEquals(10, pageInfo.getTotalPages());
        assertEquals(9, pageInfo.getNumber());
        assertFalse(pageInfo.isFirst());
        assertTrue(pageInfo.isLast());
        assertFalse(pageInfo.isHasNext());
        assertTrue(pageInfo.isHasPrevious());
    }

    @Test
    void testPageResponseDTO() {
        PageInfo pageInfo = new PageInfo(10, 0, 100, 10);
        List<ProductResponseDTO> products = new ArrayList<>();
        ProductPageResponseDTO responseDTO = new ProductPageResponseDTO(products, pageInfo);

        assertNotNull(responseDTO.getProducts());
        assertNotNull(responseDTO.getPage());
        assertEquals(pageInfo, responseDTO.getPage());
        assertEquals(products, responseDTO.getProducts());
    }
}