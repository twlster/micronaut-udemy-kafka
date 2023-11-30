package edu.udemy.micronautkafka.producer;

import edu.udemy.micronautkafka.producer.dto.ExternalQuote;
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
class TestExternalQuoteProducer {

    private static final Logger LOG = LoggerFactory.getLogger(TestExternalQuoteProducer.class);
    private static final String PROPERTY_NAME = "TestExternalQuoteProducer";

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
        final ExternalQuoteProducer producer = applicationContext.getBean(ExternalQuoteProducer.class);
        final ExternalQuoteObserver observer = applicationContext.getBean(ExternalQuoteObserver.class);

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        IntStream.range(0, 10).forEach(count -> {
            producer.send("TEST",
                    new ExternalQuote(
                            "TEST",
                            randomBigDecimal(random),
                            randomBigDecimal(random)));
        });

        Awaitility.await().untilAsserted(() -> {
            Assertions.assertEquals(10, observer.inspected.size());
        });


    }

    private BigDecimal randomBigDecimal(ThreadLocalRandom random) {
        return BigDecimal.valueOf(random.nextDouble(1, 100));
    }

    @Singleton
    @Requires(env = Environment.TEST)
    @Requires(property = PROPERTY_NAME, value = StringUtils.TRUE)
    static class ExternalQuoteObserver {
        List<ExternalQuote> inspected = new ArrayList<>();

        @KafkaListener(offsetReset = OffsetReset.EARLIEST)
        @Topic("external-quotes")
        void receive(ExternalQuote externalQuote) {
            LOG.debug("Consumed: {}", externalQuote);
            inspected.add(externalQuote);
        }
    }

}
