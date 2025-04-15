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
    private Instant timestamp;
}
