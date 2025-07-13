package com.products.infrastructure.mongo.repository;

import com.products.infrastructure.mongo.document.ProductDocument;

import java.util.List;

public interface ProductRepositoryCustom {

    List<ProductDocument> findProductsAfterCursor(Long cursor, Boolean active, String category, String name, int limit);
}
