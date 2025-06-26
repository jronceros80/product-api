package com.products.infrastructure.mapper;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private static final String DEFAULT_SORT_FIELD = "id";
    private static final String DEFAULT_SORT_DIRECTION = "asc";

    public ProductResponseDTO toResponseDTO(Product product) {
        return new ProductResponseDTO(
            product.id(),
            product.name(),
            product.price(),
            product.category(),
            product.active()
        );
    }

    public List<ProductResponseDTO> toResponseDTOList(List<Product> products) {
        return products.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    public Product toDomain(ProductEntity productEntity) {
        return new Product(
            productEntity.getId(),
            productEntity.getName(),
            productEntity.getPrice(),
            productEntity.getCategory(),
            productEntity.isActive()
        );
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity(
            product.name(),
            product.price(),
            product.category(),
            product.active()
        );
        entity.setId(product.id());
        return entity;
    }

    public Product toDomain(ProductRequestDTO requestDTO) {
        return new Product(
            null,
            requestDTO.name(),
            requestDTO.price(),
            ProductCategory.valueOf(requestDTO.category().name()),
            requestDTO.active()
        );
    }

    public PagedModel<EntityModel<ProductResponseDTO>> toPagedModel(
            final PaginatedResult<?> paginatedResult,
            final Pageable pageable,
            final PagedResourcesAssembler<ProductResponseDTO> assembler) {

        final List<ProductResponseDTO> dtoList = paginatedResult.content()
                .stream()
                .map(product -> toResponseDTO((com.products.domain.model.Product) product))
                .toList();

        final Page<ProductResponseDTO> page = new PageImpl<>(
                dtoList,
                pageable,
                paginatedResult.totalElements()
        );

        return assembler.toModel(page, EntityModel::of);
    }

    public PaginationQuery toPaginationQuery(final Pageable pageable) {
        final String sortBy = extractSortField(pageable);
        final String sortDir = extractSortDirection(pageable);

        return new PaginationQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDir
        );
    }

    public Pageable toPageable(final PaginationQuery paginationQuery) {
        Sort.Direction direction = "desc".equalsIgnoreCase(paginationQuery.sortDir()) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, paginationQuery.sortBy());
        
        return PageRequest.of(
            paginationQuery.pageNumber(), 
            paginationQuery.pageSize(), 
            sort
        );
    }

    private String extractSortField(final Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().iterator().next().getProperty();
        }
        return DEFAULT_SORT_FIELD;
    }

    private String extractSortDirection(final Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().iterator().next().getDirection().name().toLowerCase();
        }
        return DEFAULT_SORT_DIRECTION;
    }
}
