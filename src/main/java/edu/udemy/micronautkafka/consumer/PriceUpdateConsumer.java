package edu.udemy.micronautkafka.consumer;

import edu.udemy.micronautkafka.producer.dto.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@KafkaListener(
        clientId = "mn-pricing-price-update-consumer",
        groupId = "price-update-consumer",
        batch = true
)
@AllArgsConstructor
public class PriceUpdateConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PriceUpdateConsumer.class);

    @Topic("price_update")
    void consume(List<PriceUpdate> priceUpdates) {
        LOG.debug("Consumed price update {}", priceUpdates);
    }

}
