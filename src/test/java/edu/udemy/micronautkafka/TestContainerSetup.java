package edu.udemy.micronautkafka;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class TestContainerSetup {

    private static final Logger LOG = LoggerFactory.getLogger(TestContainerSetup.class);
    @Rule
    public KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Test
    void setupWorks() {
        kafka.start();
        LOG.debug("Bootstrap Servers: {}", kafka.getBootstrapServers());
        kafka.stop();
    }

}
