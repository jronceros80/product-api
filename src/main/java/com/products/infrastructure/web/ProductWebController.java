package com.products.infrastructure.web;

import com.products.application.ProductUseCase;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.mapper.ProductMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/web")
public class ProductWebController {

    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;

    public ProductWebController(final ProductUseCase productUseCase, final ProductMapper productMapper) {
        this.productUseCase = productUseCase;
        this.productMapper = productMapper;
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            Model model) {

        PaginationQuery paginationQuery = new PaginationQuery(page, size, sortBy, sortDir);
        ProductFilter filter = new ProductFilter(category, name, active);

        PaginatedResult<Product> paginatedResult = productUseCase.getAllActiveProducts(paginationQuery, filter);

        Pageable pageable = PageRequest.of(paginatedResult.pageNumber(), paginatedResult.pageSize());
        Page<Product> productPage = new PageImpl<>(paginatedResult.content(), pageable, paginatedResult.totalElements());

        model.addAttribute("page", productPage);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("filter", filter);
        model.addAttribute("categories", ProductCategory.values());

        return "products/list";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductRequestDTO("", null, null, true));
        model.addAttribute("categories", ProductCategory.values());
        return "products/create";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("product") ProductRequestDTO productRequestDTO,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", ProductCategory.values());
            return "products/create";
        }

        try {
            Product productRequest = productMapper.toDomain(productRequestDTO);
            productUseCase.createProduct(productRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Product created successfully!");
            return "redirect:/web/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating product: " + e.getMessage());
            model.addAttribute("categories", ProductCategory.values());
            return "products/create";
        }
    }

    @GetMapping("/products/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        try {
            Product product = productUseCase.getActiveProductById(id);
            model.addAttribute("product", productMapper.toResponseDTO(product));
            return "products/detail";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Product not found");
            return "redirect:/web/products";
        }
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Product product = productUseCase.getActiveProductById(id);
            ProductRequestDTO productDTO = new ProductRequestDTO(
                    product.name(),
                    product.price(),
                    product.category(),
                    product.active());
            model.addAttribute("product", productDTO);
            model.addAttribute("productId", id);
            model.addAttribute("categories", ProductCategory.values());
            return "products/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Product not found");
            return "redirect:/web/products";
        }
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
            @Valid @ModelAttribute("product") ProductRequestDTO productRequestDTO,
            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            model.addAttribute("categories", ProductCategory.values());
            return "products/edit";
        }

        try {
            Product productRequest = productMapper.toDomain(productRequestDTO);
            productUseCase.updateProduct(id, productRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/web/products";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating product: " + e.getMessage());
            model.addAttribute("productId", id);
            model.addAttribute("categories", ProductCategory.values());
            return "products/edit";
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productUseCase.deactivateProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deactivating product: " + e.getMessage());
        }
        return "redirect:/web/products";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/web/products";
    }
}