package com.products.infrastructure.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.products.domain.model.Product;
import com.products.domain.port.ProductMongoPort;
import com.products.infrastructure.kafka.avro.generated.ProductEvent;
import com.products.infrastructure.mapper.ProductMapper;

@Component
public class ProductConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductConsumer.class);

    private final ProductMongoPort productPersistenceMongoPort;

    private final ProductMapper productMapper;

    public ProductConsumer(ProductMongoPort productPersistenceMongoPort, ProductMapper productMapper) {
        this.productPersistenceMongoPort = productPersistenceMongoPort;
        this.productMapper = productMapper;
    }

    @KafkaListener(topics = "products_changes", groupId = "product-group")
    public void consume(
            @Payload ProductEvent avroMessage,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            log.info("Received Avro message from topic: {}, partition: {}, offset: {}, key: {}",
                    topic, partition, offset, key);

            final Product product = productMapper.avroToDomain(avroMessage);

            productPersistenceMongoPort.save(product);

            log.info("Successfully processed and saved product with ID: {}", product);

            acknowledgment.acknowledge();

        } catch (final Exception e) {
            log.error("Failed to process Avro message from topic: {}, key: {}", topic, key, e);
            acknowledgment.acknowledge();
        }
    }
}
