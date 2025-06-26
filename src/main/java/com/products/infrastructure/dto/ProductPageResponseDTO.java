package com.products.infrastructure.dto;

import org.springframework.hateoas.RepresentationModel;
import java.util.List;

public class ProductPageResponseDTO extends RepresentationModel<ProductPageResponseDTO> {
    private final List<ProductResponseDTO> products;
    private final PageInfo page;

    public ProductPageResponseDTO(List<ProductResponseDTO> products, PageInfo page) {
        this.products = products;
        this.page = page;
    }

    public List<ProductResponseDTO> getProducts() {
        return products;
    }

    public PageInfo getPage() {
        return page;
    }


}