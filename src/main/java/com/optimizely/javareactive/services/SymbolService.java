package com.optimizely.javareactive.services;

import com.optimizely.javareactive.documents.Symbol;
import com.optimizely.javareactive.repositories.SymbolRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SymbolService {

    private final SymbolRepository symbolRepository;
    private final WebClient webClient;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Value("${finnhub.token}")
    private String apiToken;

    public SymbolService(SymbolRepository symbolRepository, WebClient.Builder webClientBuilder, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.symbolRepository = symbolRepository;
        // Base URL for Finnhub API
        this.webClient = webClientBuilder.baseUrl("https://finnhub.io/api/v1").build();
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    /**
     * Loads master data from Finnhub if the symbols collection is empty.
     */
    public Mono<Void> loadMasterData() {
        return symbolRepository.count().flatMap(count -> {
            if (count > 0) {
                // Data already exists; skip loading.
                return Mono.empty();
            } else {
                // Fetch data from Finnhub
                return webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/stock/symbol")
                                .queryParam("exchange", "US")
                                .queryParam("token", apiToken)
                                .build())
                        .retrieve()
                        // Map the JSON response to a Flux of Symbol objects.
                        .bodyToFlux(Symbol.class)
                        // Save all symbols to MongoDB.
                        .collectList()
                        .flatMapMany(symbolRepository::saveAll)
                        .then();
            }
        });
    }


    public Flux<Symbol> getSymbols(String term, int page, int size) {
        Query query = buildQuerySearchWithTerm(term);
        query.with(PageRequest.of(page, size));

        return reactiveMongoTemplate.find(query, Symbol.class);
    }

    public Mono<Long> countSymbols(String term) {
        Query query = buildQuerySearchWithTerm(term);

        return reactiveMongoTemplate.count(query, Symbol.class);
    }

    private Query buildQuerySearchWithTerm(String term) {
        Query query = new Query();
        if (StringUtils.hasText(term)) {
            Criteria criteria = new Criteria().orOperator(
                    Criteria.where("symbol").regex(term, "i"),
                    Criteria.where("displaySymbol").regex(term, "i")
            );
            query.addCriteria(criteria);
        }
        return query;
    }
}
