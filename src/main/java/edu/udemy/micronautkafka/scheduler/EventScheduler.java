package edu.udemy.micronautkafka.scheduler;

import edu.udemy.micronautkafka.producer.ExternalQuoteProducer;
import edu.udemy.micronautkafka.producer.dto.ExternalQuote;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
@AllArgsConstructor
@Requires(notEnv = Environment.TEST)
public class EventScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(EventScheduler.class);
    private static final List<String> SYMBOLS = List.of("AAPL", "TSLA", "AMZN", "FB");

    private final ExternalQuoteProducer externalQuoteProducer;

    @Scheduled(fixedDelay = "10s")
    void generate() {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final ExternalQuote quote = new ExternalQuote(
                SYMBOLS.get(random.nextInt(0, SYMBOLS.size() - 1)),
                randomBigDecimal(random),
                randomBigDecimal(random)
        );
        LOG.debug("Sending new quote {}", quote);
        externalQuoteProducer.send(quote.getSymbol(), quote);
    }

    private BigDecimal randomBigDecimal(ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(1, 100));
    }

}
