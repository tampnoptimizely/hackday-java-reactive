package com.optimizely.javareactive.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MasterDataLoader {

    private final SymbolService symbolService;

    @Autowired
    public MasterDataLoader(SymbolService symbolService) {
        this.symbolService = symbolService;
    }

    @PostConstruct
    public void loadMasterSymbols() {
        symbolService.loadMasterData()
                .subscribe(
                        null,
                        error -> log.error("Error loading master data: " + error),
                        () -> log.info("Master data loaded successfully.")
                );
    }
}
