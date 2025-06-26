package com.products.infrastructure.web;

import com.products.application.ProductUseCase;
import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductWebControllerTest {

    @Mock
    private ProductUseCase productUseCase;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ProductWebController productWebController;

    private Product product;
    private ProductRequestDTO productRequestDTO;
    private PaginatedResult<Product> paginatedResult;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Test Product", BigDecimal.TEN, ProductCategory.BOOKS, true);
        productRequestDTO = new ProductRequestDTO("Test Product", BigDecimal.TEN, ProductCategory.BOOKS, true);
        paginatedResult = new PaginatedResult<>(Collections.singletonList(product), 1L, 1, 0, 10);
    }

    @Test
    void listProducts_ShouldAddAttributesAndReturnListView() {
        when(productUseCase.getAllActiveProducts(any(PaginationQuery.class), any(ProductFilter.class)))
                .thenReturn(paginatedResult);

        String viewName = productWebController.listProducts(0, 10, "id", "asc", "BOOKS", "Test", true, model);

        assertEquals("products/list", viewName);
        verify(model, times(6)).addAttribute(anyString(), any());
    }

    @Test
    void showCreateForm_ShouldReturnCreateView() {
        String viewName = productWebController.showCreateForm(model);

        assertEquals("products/create", viewName);
        verify(model).addAttribute(eq("product"), any(ProductRequestDTO.class));
        verify(model).addAttribute(eq("categories"), eq(ProductCategory.values()));
    }

    @Test
    void createProduct_WithValidData_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productMapper.toDomain(productRequestDTO)).thenReturn(product);

        String viewName = productWebController.createProduct(
                productRequestDTO, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/web/products", viewName);
        verify(productUseCase).createProduct(product);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Product created successfully!");
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturnCreateView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = productWebController.createProduct(
                productRequestDTO, bindingResult, model, redirectAttributes);

        assertEquals("products/create", viewName);
        verify(model).addAttribute("categories", ProductCategory.values());
        verify(productUseCase, never()).createProduct(any());
    }

    @Test
    void showProduct_WhenProductDoesNotExist_ShouldRedirect() {
        when(productUseCase.getActiveProductById(1L)).thenThrow(new ProductNotFoundException("Not found"));

        String viewName = productWebController.showProduct(1L, model);

        assertEquals("redirect:/web/products", viewName);
        verify(model).addAttribute("errorMessage", "Product not found");
    }

    @Test
    void showEditForm_WhenProductDoesNotExist_ShouldRedirect() {
        when(productUseCase.getActiveProductById(1L)).thenThrow(new ProductNotFoundException("Not found"));

        String viewName = productWebController.showEditForm(1L, model);

        assertEquals("redirect:/web/products", viewName);
        verify(model).addAttribute("errorMessage", "Product not found");
    }

    @Test
    void updateProduct_WithValidData_ShouldRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(productMapper.toDomain(productRequestDTO)).thenReturn(product);

        String viewName = productWebController.updateProduct(
                1L, productRequestDTO, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/web/products", viewName);
        verify(productUseCase).updateProduct(1L, product);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Product updated successfully!");
    }

    @Test
    void updateProduct_WithInvalidData_ShouldReturnEditView() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = productWebController.updateProduct(
                1L, productRequestDTO, bindingResult, model, redirectAttributes);

        assertEquals("products/edit", viewName);
        verify(model).addAttribute("productId", 1L);
        verify(model).addAttribute("categories", ProductCategory.values());
        verify(productUseCase, never()).updateProduct(anyLong(), any());
    }

    @Test
    void deleteProduct_ShouldRedirectAndDeactivate() {
        String viewName = productWebController.deleteProduct(1L, redirectAttributes);

        assertEquals("redirect:/web/products", viewName);
        verify(productUseCase).deactivateProduct(1L);
        verify(redirectAttributes).addFlashAttribute("successMessage", "Product deactivated successfully!");
    }

    @Test
    void deleteProduct_WhenUseCaseThrowsException_ShouldRedirectWithError() {
        doThrow(new RuntimeException("DB error")).when(productUseCase).deactivateProduct(1L);

        String viewName = productWebController.deleteProduct(1L, redirectAttributes);

        assertEquals("redirect:/web/products", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Error deactivating product: DB error");
    }

    @Test
    void home_ShouldRedirect() {
        String viewName = productWebController.home();
        assertEquals("redirect:/web/products", viewName);
    }
}