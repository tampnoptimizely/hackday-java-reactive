package com.optimizely.javareactive.repositories;

import com.optimizely.javareactive.documents.Symbol;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SymbolRepository extends ReactiveCrudRepository<Symbol, String> {
    Mono<Long> count();
}
