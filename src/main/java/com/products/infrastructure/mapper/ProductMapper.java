package com.products.infrastructure.mapper;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.infrastructure.dto.PageInfo;
import com.products.infrastructure.dto.ProductPageResponseDTO;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductResponseDTO toResponseDTO(final Product product) {
        return new ProductResponseDTO(
                product.id(),
                product.name(),
                product.price(),
                product.category(),
                product.active());
    }

    public Product toDomain(final ProductEntity productEntity) {
        return new Product(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getCategory(),
                productEntity.isActive());
    }

    public ProductEntity toEntity(final Product product) {
        ProductEntity entity = new ProductEntity(
                product.name(),
                product.price(),
                product.category(),
                product.active());
        entity.setId(product.id());
        return entity;
    }

    public Product toDomain(final ProductRequestDTO requestDTO) {
        return new Product(
                null,
                requestDTO.name(),
                requestDTO.price(),
                ProductCategory.valueOf(requestDTO.category().name()),
                requestDTO.active());
    }

    public ProductPageResponseDTO toPageResponseDTO(final PaginatedResult<Product> paginatedResult) {
        final List<ProductResponseDTO> dtoList = paginatedResult.content()
                .stream()
                .map(this::toResponseDTO)
                .toList();

        final PageInfo pageInfo = new PageInfo(
                paginatedResult.size(),
                paginatedResult.limit(),
                paginatedResult.hasNext(),
                paginatedResult.hasPrevious(),
                paginatedResult.nextCursor(),
                paginatedResult.previousCursor());

        return new ProductPageResponseDTO(
                dtoList,
                paginatedResult.nextCursor(),
                paginatedResult.previousCursor(),
                paginatedResult.hasNext(),
                paginatedResult.hasPrevious(),
                paginatedResult.size(),
                paginatedResult.limit(),
                pageInfo);
    }

    public PaginationQuery toPaginationQuery(
            final String cursor, final Integer limit, final String sortBy, final String sortDir) {

        final int actualLimit = (limit != null && limit > 0 && limit <= 100) ? limit : 20;
        final String actualSortBy = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "id";
        final String actualSortDir = (sortDir != null && !sortDir.trim().isEmpty()) ? sortDir : "asc";

        return new PaginationQuery(cursor, actualLimit, actualSortBy, actualSortDir);
    }
}
