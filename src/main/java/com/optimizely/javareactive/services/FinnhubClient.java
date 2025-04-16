package com.optimizely.javareactive.services;

import com.optimizely.javareactive.documents.StockPrice;
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
                .map(response -> StockPrice
                        .builder()
                        .symbol(symbol)
                        .price(response.getCurrentPrice())
                        .percentChange(response.getPercentChange())
                        .highPrice(response.getHighOfDay())
                        .lowPrice(response.getLowOfDay())
                        .openPrice(response.getOpenPriceOfDay())
                        .timestamp(Instant.now())
                        .build());
    }

    @Setter
    private static class FinnhubResponse {
        private double c;
        private double dp;
        private double h;
        private double l;
        private double o;

        public double getCurrentPrice() {
            return c;
        }
        public double getPercentChange() {
            return dp;
        }
        public double getHighOfDay() {
            return h;
        }

        public double getLowOfDay() {
            return l;
        }

        public double getOpenPriceOfDay() {
            return o;
        }
    }
}