package com.products.integration.config;

import com.products.domain.port.ProductKafkaPort;
import com.products.domain.port.ProductMongoPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    @Primary
    public ProductKafkaPort productKafkaPort() {
        return mock(ProductKafkaPort.class);
    }

    @Bean
    @Primary
    public ProductMongoPort productMongoPort() {
        return mock(ProductMongoPort.class);
    }
}