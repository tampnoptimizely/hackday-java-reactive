package com.optimizely.javareactive;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class FinnhubClient {

    private final WebClient webClient;

    @Value("${finnhub.token}")
    private String apiKey;

    public FinnhubClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://finnhub.io/api/v1").build();
    }

    public Mono<StockPrice> getQuote(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubResponse.class)
                .map(response -> new StockPrice(symbol, response.getCurrentPrice(), Instant.now()));
    }

    @Setter
    private static class FinnhubResponse {
        private double c;

        public double getCurrentPrice() {
            return c;
        }

    }
}