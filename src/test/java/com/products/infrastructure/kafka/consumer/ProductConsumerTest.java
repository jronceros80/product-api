package com.products.infrastructure.kafka.consumer;

import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.port.ProductMongoPort;
import com.products.infrastructure.kafka.avro.generated.ProductEvent;
import com.products.infrastructure.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductConsumerTest {

    @Mock
    private ProductMongoPort productMongoPort;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private ProductConsumer productConsumer;

    private ProductEvent productEvent;
    private Product product;

    @BeforeEach
    void setUp() {
        productEvent = ProductEvent.newBuilder()
                .setId(1L)
                .setName("Test Product")
                .setPrice(BigDecimal.valueOf(99.99))
                .setCategory("ELECTRONICS")
                .setActive(true)
                .build();

        product = new Product(
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                ProductCategory.ELECTRONICS,
                true);
    }

    @Test
    void consume_ShouldProcessMessageSuccessfully() {
        String key = "product-1";
        String topic = "products_changes";
        int partition = 0;
        long offset = 123L;

        when(productMapper.avroToDomain(productEvent)).thenReturn(product);

        productConsumer.consume(productEvent, key, topic, partition, offset, acknowledgment);

        verify(productMapper).avroToDomain(productEvent);
        verify(productMongoPort).save(product);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void consume_ShouldHandleExceptionAndAcknowledge() {
        String key = "product-1";
        String topic = "products_changes";
        int partition = 0;
        long offset = 123L;

        when(productMapper.avroToDomain(productEvent)).thenThrow(new RuntimeException("Mapping error"));

        productConsumer.consume(productEvent, key, topic, partition, offset, acknowledgment);

        verify(productMapper).avroToDomain(productEvent);
        verify(productMongoPort, never()).save(any(Product.class));
        verify(acknowledgment).acknowledge();
    }

    @Test
    void consume_ShouldHandleSaveExceptionAndAcknowledge() {
        String key = "product-1";
        String topic = "products_changes";
        int partition = 0;
        long offset = 123L;

        when(productMapper.avroToDomain(productEvent)).thenReturn(product);
        doThrow(new RuntimeException("Database error")).when(productMongoPort).save(product);

        productConsumer.consume(productEvent, key, topic, partition, offset, acknowledgment);

        verify(productMapper).avroToDomain(productEvent);
        verify(productMongoPort).save(product);
        verify(acknowledgment).acknowledge();
    }
}