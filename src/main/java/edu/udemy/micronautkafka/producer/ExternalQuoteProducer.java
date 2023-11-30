package edu.udemy.micronautkafka.producer;

import edu.udemy.micronautkafka.producer.dto.ExternalQuote;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient(id = "external-quoter-producer")
public interface ExternalQuoteProducer {

    @Topic("external-quotes")
    void send(@KafkaKey String symbol, ExternalQuote externalQuote);

}
