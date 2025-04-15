package com.optimizely.javareactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final FinnhubClient finnhubClient;

    @Autowired
    public StockController(FinnhubClient finnhubClient) {
        this.finnhubClient = finnhubClient;
    }

    // Endpoint to get the price for a single stock symbol
    @GetMapping("/{symbol}/price")
    public Mono<StockPrice> getPrice(@PathVariable String symbol) {
        return finnhubClient.getQuote(symbol);
    }

    // Endpoint to get prices for multiple stock symbols, e.g., /stocks/prices?symbols=AAPL,GOOG,MSFT
    @GetMapping("/prices")
    public Flux<StockPrice> getPrices(@RequestParam String symbols) {
        List<String> symbolList = Arrays.asList(symbols.split(","));
        return Flux.fromIterable(symbolList)
                .flatMap(finnhubClient::getQuote);
    }
}
