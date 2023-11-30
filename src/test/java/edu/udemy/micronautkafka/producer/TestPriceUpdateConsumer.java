package edu.udemy.micronautkafka.producer;

import edu.udemy.micronautkafka.producer.dto.ExternalQuote;
import edu.udemy.micronautkafka.producer.dto.PriceUpdate;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.awaitility.Awaitility;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Testcontainers
class TestPriceUpdateConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(TestPriceUpdateConsumer.class);
    private static final String PROPERTY_NAME = "TestPriceUpdateConsumer";

    @Rule
    public static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    private static ApplicationContext applicationContext;

    @BeforeEach
    void start() {
        kafka.start();
        LOG.debug("Bootstrap Servers: {}", kafka.getBootstrapServers());

        applicationContext = ApplicationContext.run(
                CollectionUtils.mapOf(
                        "kafka.bootstrap.servers", kafka.getBootstrapServers(),
                        PROPERTY_NAME, StringUtils.TRUE
                ),
                Environment.TEST
        );

    }

    @AfterEach
    void stop() {
        kafka.stop();
        applicationContext.close();
    }

    @Test
    void setupWorks() {
        final TestScopeExternalQuoteProducer externalQuoteProducer = applicationContext.getBean(TestScopeExternalQuoteProducer.class);
        final PriceUpdateObserver observer = applicationContext.getBean(PriceUpdateObserver.class);

        final ThreadLocalRandom random = ThreadLocalRandom.current();

        IntStream.range(0, 4).forEach(count -> {
            externalQuoteProducer.send(
                    new ExternalQuote(
                            "TEST",
                            randomBigDecimal(random),
                            randomBigDecimal(random)));
        });
        Awaitility.await().untilAsserted(() -> {
            Assertions.assertEquals(4, observer.inspected.size());
        });


    }

    private BigDecimal randomBigDecimal(ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(1, 100));
    }

    @Singleton
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
    static class PriceUpdateObserver {
        List<PriceUpdate> inspected = new ArrayList<>();

        @KafkaListener(offsetReset = OffsetReset.EARLIEST)
        @Topic("price_update")
        void receive(PriceUpdate priceUpdate) {
            LOG.debug("Consumed: {}", priceUpdate);
            inspected.add(priceUpdate);
        }
    }

    @KafkaClient
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
    static interface TestScopeExternalQuoteProducer {
        @Topic("external-quotes")
        void send(ExternalQuote externalQuote);
    }

}
