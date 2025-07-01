package com.products.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.application.ProductUseCase;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.infrastructure.dto.PageInfo;
import com.products.infrastructure.dto.ProductPageResponseDTO;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.mapper.ProductMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.DisplayName;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

        @Mock
        private ProductUseCase productUseCase;

        @Mock
        private ProductMapper productMapper;

        @InjectMocks
        private ProductController productController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(productController)
                                .build();
                objectMapper = new ObjectMapper();
        }

        @Test
        void createProduct_ShouldReturnCreatedProduct_WhenValidRequest() throws Exception {
                ProductRequestDTO requestDTO = new ProductRequestDTO(
                                "Test Product",
                                BigDecimal.valueOf(99.99),
                                ProductCategory.ELECTRONICS,
                                true);

                Product domainProduct = new Product(
                                "Test Product",
                                BigDecimal.valueOf(99.99),
                                ProductCategory.ELECTRONICS);

                Product createdProduct = new Product(
                                1L,
                                "Test Product",
                                BigDecimal.valueOf(99.99),
                                ProductCategory.ELECTRONICS,
                                true);

                ProductResponseDTO responseDTO = new ProductResponseDTO(
                                1L,
                                "Test Product",
                                BigDecimal.valueOf(99.99),
                                ProductCategory.ELECTRONICS,
                                true);

                when(productMapper.toDomain(any(ProductRequestDTO.class))).thenReturn(domainProduct);
                when(productUseCase.createProduct(any(Product.class))).thenReturn(createdProduct);
                when(productMapper.toResponseDTO(any(Product.class))).thenReturn(responseDTO);

                mockMvc.perform(post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Test Product"));
        }

        @Test
        @DisplayName("Should return products with cursor pagination")
        void getAllActiveProducts_ShouldReturnProductsWithCursorPagination() throws Exception {
                List<Product> products = Arrays.asList(
                                new Product(1L, "Laptop", BigDecimal.valueOf(1000), ProductCategory.ELECTRONICS, true),
                                new Product(2L, "Mouse", BigDecimal.valueOf(25), ProductCategory.ELECTRONICS, true));

                PaginatedResult<Product> paginatedResult = new PaginatedResult<>(
                                products, "2", null, false, false, 2, 20);

                PaginationQuery expectedPaginationQuery = new PaginationQuery(null, 20, "id", "asc");
                ProductFilter expectedFilter = new ProductFilter("ELECTRONICS", "laptop", true);

                when(productMapper.toPaginationQuery(null, 20, "id", "asc")).thenReturn(expectedPaginationQuery);
                when(productUseCase.getAllActiveProducts(expectedPaginationQuery, expectedFilter))
                                .thenReturn(paginatedResult);
                when(productMapper.toPageResponseDTO(paginatedResult)).thenReturn(
                                new ProductPageResponseDTO(
                                                Arrays.asList(
                                                                new ProductResponseDTO(1L, "Laptop",
                                                                                BigDecimal.valueOf(1000),
                                                                                ProductCategory.ELECTRONICS, true),
                                                                new ProductResponseDTO(2L, "Mouse",
                                                                                BigDecimal.valueOf(25),
                                                                                ProductCategory.ELECTRONICS, true)),
                                                "2", null, false, false, 2, 20,
                                                new PageInfo(2, 20, false, false, "2", null)));

                mockMvc.perform(get("/api/v1/products")
                                .param("limit", "20")
                                .param("sortBy", "id")
                                .param("sortDir", "asc")
                                .param("category", "ELECTRONICS")
                                .param("name", "laptop")
                                .param("active", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(2)))
                                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                                .andExpect(jsonPath("$.content[1].name").value("Mouse"))
                                .andExpect(jsonPath("$.size").value(2))
                                .andExpect(jsonPath("$.limit").value(20))
                                .andExpect(jsonPath("$.hasNext").value(false))
                                .andExpect(jsonPath("$.hasPrevious").value(false))
                                .andExpect(jsonPath("$.nextCursor").value("2"));

                verify(productUseCase).getAllActiveProducts(expectedPaginationQuery, expectedFilter);
                verify(productMapper).toPaginationQuery(null, 20, "id", "asc");
                verify(productMapper).toPageResponseDTO(paginatedResult);
        }

        @Test
        void getProductById_ShouldReturnOk_WhenProductExists() throws Exception {
                Long productId = 1L;
                Product product = new Product(
                                productId, "Test Product", BigDecimal.valueOf(99.99), ProductCategory.ELECTRONICS,
                                true);
                ProductResponseDTO responseDTO = new ProductResponseDTO(
                                productId, "Test Product", BigDecimal.valueOf(99.99), ProductCategory.ELECTRONICS,
                                true);

                when(productUseCase.getActiveProductById(productId)).thenReturn(product);
                when(productMapper.toResponseDTO(product)).thenReturn(responseDTO);

                mockMvc.perform(get("/api/v1/products/{id}", productId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(productId))
                                .andExpect(jsonPath("$.name").value("Test Product"));
        }

        @Test
        void updateProduct_ShouldReturnUpdatedProduct_WhenValidRequest() throws Exception {
                Long productId = 1L;
                ProductRequestDTO requestDTO = new ProductRequestDTO(
                                "Updated Product", BigDecimal.valueOf(149.99), ProductCategory.BOOKS, true);
                Product domainProduct = new Product(
                                "Updated Product", BigDecimal.valueOf(149.99), ProductCategory.BOOKS);
                Product updatedProduct = new Product(
                                productId, "Updated Product", BigDecimal.valueOf(149.99), ProductCategory.BOOKS, true);
                ProductResponseDTO responseDTO = new ProductResponseDTO(
                                productId, "Updated Product", BigDecimal.valueOf(149.99), ProductCategory.BOOKS, true);

                when(productMapper.toDomain(any(ProductRequestDTO.class))).thenReturn(domainProduct);
                when(productUseCase.updateProduct(any(Long.class), any(Product.class))).thenReturn(updatedProduct);
                when(productMapper.toResponseDTO(any(Product.class))).thenReturn(responseDTO);

                mockMvc.perform(put("/api/v1/products/{id}", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(productId))
                                .andExpect(jsonPath("$.name").value("Updated Product"));
        }

        @Test
        void deactivateProduct_ShouldReturnNoContent_WhenValidRequest() throws Exception {
                Long productId = 1L;
                doNothing().when(productUseCase).deactivateProduct(productId);

                mockMvc.perform(delete("/api/v1/products/{id}", productId))
                                .andExpect(status().isNoContent());

                verify(productUseCase).deactivateProduct(productId);
        }
}