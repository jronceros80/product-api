package com.products.infrastructure.mapper;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.infrastructure.dto.PageInfo;
import com.products.infrastructure.dto.ProductPageResponseDTO;
import com.products.infrastructure.dto.ProductRequestDTO;
import com.products.infrastructure.dto.ProductResponseDTO;
import com.products.infrastructure.kafka.avro.generated.ProductEvent;
import com.products.infrastructure.mongo.document.ProductDocument;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

        public ProductResponseDTO domainToResponseDTO(final Product product) {
                return new ProductResponseDTO(
                                product.id(),
                                product.name(),
                                product.price(),
                                product.category(),
                                product.active());
        }

        public ProductEntity domainToEntity(final Product product) {
                ProductEntity entity = new ProductEntity(
                                product.name(),
                                product.price(),
                                product.category(),
                                product.active());
                entity.setId(product.id());
                return entity;
        }

        public Product entityToDomain(ProductEntity productEntity) {
                return new Product(
                        productEntity.getId(),
                        productEntity.getName(),
                        productEntity.getPrice(),
                        productEntity.getCategory(),
                        productEntity.isActive());
        }

        public Product requestDtoToDomain(final ProductRequestDTO requestDTO) {
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
                                .map(this::domainToResponseDTO)
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

        public ProductEvent domainToAvro(final Product product) {
                return ProductEvent.newBuilder()
                                .setId(product.id())
                                .setName(product.name())
                                .setPrice(product.price())
                                .setActive(product.active())
                                .setCategory(product.category().name())
                                .build();
        }

        public Product avroToDomain(final ProductEvent productEvent) {
                return new Product(
                                productEvent.getId(),
                                productEvent.getName(),
                                productEvent.getPrice(),
                                ProductCategory.valueOf(productEvent.getCategory()),
                                productEvent.getActive());
        }

        public ProductDocument domainToDocument(final Product product) {
                return new ProductDocument(
                                product.id(),
                                product.name(),
                                product.price(),
                                product.category().name(),
                                product.active());
        }

        public Product documentToDomain(final ProductDocument document) {
                return new Product(
                        document.getId(),
                        document.getName(),
                        document.getPrice(),
                        ProductCategory.valueOf(document.getCategory()),
                        document.getActive());
        }
}
