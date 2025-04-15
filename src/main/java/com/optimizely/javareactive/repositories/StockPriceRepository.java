package com.optimizely.javareactive.repositories;

import com.optimizely.javareactive.documents.StockPriceEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface StockPriceRepository extends ReactiveCrudRepository<StockPriceEntity, Long> {
}
