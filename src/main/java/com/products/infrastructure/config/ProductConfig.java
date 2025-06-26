package com.products.infrastructure.config;

import com.products.application.ProductUseCase;
import com.products.domain.port.ProductPersistencePort;
import com.products.domain.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    @Bean
    public ProductService productService(ProductPersistencePort productPersistencePort) {
        return new ProductService(productPersistencePort);
    }

    @Bean
    public ProductUseCase productUseCase(ProductService productService) {
        return new ProductUseCase(productService);
    }
}