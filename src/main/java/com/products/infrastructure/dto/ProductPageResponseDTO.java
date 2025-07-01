package com.products.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Paginated response for products using cursor-based pagination")
public record ProductPageResponseDTO(

        @Schema(description = "List of products") List<ProductResponseDTO> content,

        @Schema(description = "Cursor for the next page (null if no next page)") String nextCursor,

        @Schema(description = "Cursor for the previous page (null if no previous page)") String previousCursor,

        @Schema(description = "Whether there are more items after this page") boolean hasNext,

        @Schema(description = "Whether there are items before this page") boolean hasPrevious,

        @Schema(description = "Number of items in this page") int size,

        @Schema(description = "Maximum number of items per page") int limit,

        @Schema(description = "Pagination metadata") PageInfo pageInfo) {
}