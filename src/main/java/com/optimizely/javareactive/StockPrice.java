package com.optimizely.javareactive;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StockPrice {
    private String symbol;
    private double price;
    private Instant timestamp;
}
