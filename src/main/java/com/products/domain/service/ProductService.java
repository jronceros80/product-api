package com.products.domain.service;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductPersistencePort;

public class ProductService {

    private final ProductPersistencePort productPersistencePort;

    public ProductService(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    public Product createProduct(final Product product) {
        return productPersistencePort.save(product);
    }

    public PaginatedResult<Product> getAllActiveProducts(final PaginationQuery paginationQuery,
                                                         final ProductFilter filter) {

    return productPersistencePort.findActiveProducts(paginationQuery, filter);
}

    public Product getActiveProductById(final Long id) {
        return productPersistencePort.findActiveById(id)
                .orElseThrow(() -> new ProductNotFoundException("Active product not found with id: " + id));
    }

    public Product updateProduct(final Long id, final Product productUpdate) {
        final Product existingProduct = productPersistencePort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        final Product updatedProduct = new Product(
                existingProduct.id(),
                productUpdate.name(),
                productUpdate.price(),
                productUpdate.category(),
                productUpdate.active());

        return productPersistencePort.save(updatedProduct);
    }

    public void deactivateProduct(final Long id) {
        productPersistencePort.deactivateProduct(id);
    }
}