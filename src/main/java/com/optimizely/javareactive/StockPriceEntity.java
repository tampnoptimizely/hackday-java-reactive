package com.optimizely.javareactive;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;

@Data
@Builder
@Table("stock_prices")
public class StockPriceEntity {
    @Id
    private Long id;
    private String symbol;
    private double price;
    private Instant timestamp;
}
