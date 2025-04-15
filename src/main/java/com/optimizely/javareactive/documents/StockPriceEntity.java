package com.optimizely.javareactive.documents;


import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class StockPriceEntity {
    private Long id;
    private String symbol;
    private double price;
    private Instant timestamp;
}
