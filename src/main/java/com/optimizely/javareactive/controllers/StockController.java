package com.optimizely.javareactive.controllers;

import com.optimizely.javareactive.documents.Symbol;
import com.optimizely.javareactive.payloads.PageResponse;
import com.optimizely.javareactive.services.FinnhubClient;
import com.optimizely.javareactive.documents.StockPrice;
import com.optimizely.javareactive.services.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final FinnhubClient finnhubClient;
    private final SymbolService symbolService;

    @Autowired
    public StockController(FinnhubClient finnhubClient, SymbolService symbolService) {
        this.finnhubClient = finnhubClient;
        this.symbolService = symbolService;
    }

    @GetMapping("/symbols")
    public Mono<PageResponse<Object>> getSymbols(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q
    ) {
        Flux<Symbol> listSymbols = symbolService.getSymbols(q, page, size);
        Mono<Long> totalSymbols = symbolService.countSymbols(q);
        return listSymbols.collectList()
                .zipWith(totalSymbols, (symbols, total) -> PageResponse.builder()
                        .total(total)
                        .data(new ArrayList<>(symbols))
                        .page(page)
                        .size(size)
                        .build());
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
                .flatMap(finnhubClient::getQuote)
                .collectSortedList(Comparator.comparing(StockPrice::getSymbol))
                .flatMapMany(Flux::fromIterable);
    }

}
