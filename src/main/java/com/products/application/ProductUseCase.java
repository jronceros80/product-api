package com.products.application;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;
import com.products.domain.service.ProductService;

public class ProductUseCase {

    private final ProductService productService;

    public ProductUseCase(ProductService productService) {
        this.productService = productService;
    }

    public Product createProduct(final Product product) {
        return productService.createProduct(product);
    }

    public PaginatedResult<Product> getAllActiveProducts(final PaginationQuery paginationQuery,
                                                         final ProductFilter filter) {

    return productService.getAllActiveProducts(paginationQuery, filter);
}

    public Product getActiveProductById(final Long id) {
        return productService.getActiveProductById(id);
    }

    public Product updateProduct(final Long id, final Product productRequest) {
        return productService.updateProduct(id, productRequest);
    }

    public void deactivateProduct(final Long id) {
        productService.deactivateProduct(id);
    }
}