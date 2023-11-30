package edu.udemy.micronautkafka.producer.dto;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
public class PriceUpdate {

    private String symbol;
    private BigDecimal lastPrice;

}
