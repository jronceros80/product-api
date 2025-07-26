package com.products.infrastructure.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.products.domain.model.Product;
import com.products.domain.port.ProductKafkaPort;
import com.products.infrastructure.kafka.avro.generated.ProductEvent;
import com.products.infrastructure.mapper.ProductMapper;

@Component
public class ProductProducer implements ProductKafkaPort {

    private static final Logger log = LoggerFactory.getLogger(ProductProducer.class);
    private static final String TOPIC_NAME = "products_changes";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductMapper productMapper;

    public ProductProducer(KafkaTemplate<String, Object> kafkaTemplate, ProductMapper productMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.productMapper = productMapper;
    }

    @Override
    public void publishEvent(Product product) {
        final ProductEvent avroMessage = productMapper.domainToAvro(product);
        final String productId = product.id().toString();
        try {
            kafkaTemplate.send(TOPIC_NAME, productId, avroMessage)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully published avro product with ID: {} to topic: {}",
                                    productId, TOPIC_NAME);
                        } else {
                            log.error("Failed to publish avro product with ID: {} to topic: {}",
                                    productId, TOPIC_NAME, ex);
                        }
                    });

        } catch (final Exception e) {
            log.error("Failed to create avro product with ID: {}", productId, e);
            throw new RuntimeException("Failed to create avro product", e);
        }
    }
}
