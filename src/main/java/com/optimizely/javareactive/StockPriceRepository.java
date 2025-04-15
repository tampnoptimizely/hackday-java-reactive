package com.optimizely.javareactive;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockPriceRepository extends ReactiveCrudRepository<StockPriceEntity, Long> {
    Flux<StockPriceEntity> findBySymbolOrderByTimestampDesc(String symbol);
}
