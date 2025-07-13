package com.products.infrastructure.adapter;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductMongoPort;
import com.products.infrastructure.mapper.ProductMapper;
import com.products.infrastructure.mongo.document.ProductDocument;
import com.products.infrastructure.mongo.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductMongoAdapter implements ProductMongoPort {

    private static final Logger logger = LoggerFactory.getLogger(ProductMongoAdapter.class);

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public ProductMongoAdapter(ProductMapper productMapper, ProductRepository productRepository) {
        this.productMapper = productMapper;
        this.productRepository = productRepository;
    }

    @Override
    public void save(Product product) {
        ProductDocument document = productMapper.domainToDocument(product);
        productRepository.save(document);
    }

    @Override
    public Optional<Product> findById(final Long id) {
        return productRepository.findById(id)
                .map(productMapper::documentToDomain);
    }

    @Override
    public Optional<Product> findActiveById(final Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(productMapper::documentToDomain);
    }

    @Override
    public PaginatedResult<Product> findActiveProducts(final PaginationQuery paginationQuery,
                                                       final ProductFilter filter) {

        final Long cursor = parseCursor(paginationQuery.cursor());
        final int limit = paginationQuery.limit();
        final String categoryStr = filter.getCategoryForQuery();
        final String name = filter.getNameForQuery();
        final Boolean active = filter.active();

        final List<ProductDocument> documents = productRepository.findProductsAfterCursor(
                cursor, active, categoryStr, name, limit + 1);

        final boolean hasNext = documents.size() > limit;
        final List<ProductDocument> actualDocuments = hasNext ? documents.subList(0, limit) : documents;

        final List<Product> products = actualDocuments.stream()
                .map(productMapper::documentToDomain)
                .toList();

        String nextCursor = null;
        String previousCursor = null;
        boolean hasPrevious = cursor != null;

        if (!products.isEmpty()) {
            nextCursor = String.valueOf(products.getLast().id());
        }

        if (hasPrevious && !products.isEmpty()) {
            previousCursor = String.valueOf(products.getFirst().id());
        }

        return new PaginatedResult<>(
                products,
                nextCursor,
                previousCursor,
                hasNext,
                hasPrevious,
                products.size(),
                limit);
    }

    private Long parseCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(cursor.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid cursor format: {}", cursor);
            return null;
        }
    }
}