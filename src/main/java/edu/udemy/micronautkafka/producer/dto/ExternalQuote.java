package edu.udemy.micronautkafka.producer.dto;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@Introspected
public class ExternalQuote {

    private String symbol;
    private BigDecimal lastPrice;
    private BigDecimal volumen;

}
