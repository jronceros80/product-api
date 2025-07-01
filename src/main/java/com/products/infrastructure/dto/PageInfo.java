package com.products.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination information for cursor-based navigation")
public record PageInfo(
        @Schema(description = "Number of items in current page") int size,

        @Schema(description = "Maximum items per page") int limit,

        @Schema(description = "Whether there is a next page") boolean hasNext,

        @Schema(description = "Whether there is a previous page") boolean hasPrevious,

        @Schema(description = "Cursor for next page navigation") String nextCursor,

        @Schema(description = "Cursor for previous page navigation") String previousCursor) {
}