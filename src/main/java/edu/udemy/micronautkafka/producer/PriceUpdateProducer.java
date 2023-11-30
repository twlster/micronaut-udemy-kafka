package edu.udemy.micronautkafka.producer;

import edu.udemy.micronautkafka.producer.dto.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.async.publisher.Publishers;

import java.util.List;

@KafkaClient(batch = true)
public interface PriceUpdateProducer {

    @Topic("price_update")
    Publishers send(List<PriceUpdate> prices);

}
