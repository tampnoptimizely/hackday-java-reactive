package com.optimizely.javareactive.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class StockPrice {
    private String symbol;
    private double price;
    private double percentChange;
    private double highPrice;
    private double lowPrice;
    private double openPrice;
    private Instant timestamp;
}
