package com.products.infrastructure.config;

import com.products.application.ProductUseCase;
import com.products.domain.port.ProductPostgresPort;
import com.products.domain.port.ProductKafkaPort;
import com.products.domain.port.ProductMongoPort;
import com.products.domain.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    @Bean
    public ProductService productService(
            final ProductPostgresPort productPostgresPort,
            final ProductMongoPort productMongoPort,
            final ProductKafkaPort productKafkaPort) {
        return new ProductService(productPostgresPort, productMongoPort, productKafkaPort);
    }

    @Bean
    public ProductUseCase productUseCase(final ProductService productService) {
        return new ProductUseCase(productService);
    }
}