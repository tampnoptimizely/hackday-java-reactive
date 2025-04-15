package com.optimizely.javareactive;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockFetcherService {
    private final FinnhubClient client;
    private final StockPriceRepository repository;

    public StockFetcherService(FinnhubClient client, StockPriceRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    private final List<String> symbols = List.of("AAPL", "GOOG", "MSFT");

    public void fetchStockPrices() {
        symbols.forEach(symbol ->
                client.getQuote(symbol)
                        .map(sp -> new StockPriceEntity(null, sp.getSymbol(), sp.getPrice(), sp.getTimestamp()))
                        .flatMap(repository::save)
                        .subscribe()
        );
    }
}
