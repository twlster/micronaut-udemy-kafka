package edu.udemy.micronautkafka.consumer;

import edu.udemy.micronautkafka.producer.PriceUpdateProducer;
import edu.udemy.micronautkafka.producer.dto.ExternalQuote;
import edu.udemy.micronautkafka.producer.dto.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@KafkaListener(
        clientId = "mn-pricing-external-quote-consumer",
        groupId = "external-quote-consumer",
        batch = true,
        offsetReset = OffsetReset.EARLIEST
)
@AllArgsConstructor
public class ExternalQuoteConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalQuoteConsumer.class);

    private final PriceUpdateProducer priceUpdateProducer;

    @Topic("external-quotes")
    void consume(List<ExternalQuote> externalQuotes) {
        LOG.debug("Consumed external quoters {}", externalQuotes);
        final List<PriceUpdate> priceUpdates =
                externalQuotes
                        .stream()
                        .map(quote -> new PriceUpdate(quote.getSymbol(), quote.getLastPrice()))
                        .toList();
        priceUpdateProducer
                .send(priceUpdates);
//                .doOnError(error -> LOG.error("Error producing price update message: {}", error.getCause()))
//                .forEach(recordMetadata ->
//                        LOG.debug("Record sent to topic {} on offet {}", recordMetadata.topic(), recordMetadata.offset()));
    }

}
