package com.products.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.application.ProductUseCase;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

        @Mock
        private ProductUseCase productUseCase;

        @Mock
        private ProductMapper productMapper;

        @Mock
        private PagedResourcesAssembler<ProductResponseDTO> pagedResourcesAssembler;

        @InjectMocks
        private ProductController productController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                mockMvc = MockMvcBuilders.standaloneSetup(productController)
                                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
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
        void getAllActiveProducts_ShouldReturnOk_WhenValidRequest() throws Exception {
                Product product = new Product(
                                1L, "Test Product", BigDecimal.valueOf(99.99), ProductCategory.ELECTRONICS, true);
                ProductResponseDTO responseDTO = new ProductResponseDTO(
                                1L, "Test Product", BigDecimal.valueOf(99.99), ProductCategory.ELECTRONICS, true);

                PaginatedResult<Product> paginatedResult = new PaginatedResult<>(List.of(product), 1L, 1, 0, 10);
                PaginationQuery mockPaginationQuery = new PaginationQuery(0, 10, "id", "asc");
                PagedModel<EntityModel<ProductResponseDTO>> mockPagedModel = PagedModel.empty();

                when(productMapper.toPaginationQuery(any())).thenReturn(mockPaginationQuery);
                when(productUseCase.getAllActiveProducts(any(PaginationQuery.class), any(ProductFilter.class)))
                                .thenReturn(paginatedResult);
                when(productMapper.toPagedModel(any(), any(), any())).thenReturn(mockPagedModel);

                mockMvc.perform(get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "10")
                                .param("category", "ELECTRONICS"))
                                .andExpect(status().isOk());
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