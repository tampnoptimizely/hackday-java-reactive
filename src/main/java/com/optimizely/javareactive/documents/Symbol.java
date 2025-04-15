package com.optimizely.javareactive.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "symbols")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Symbol {
    @Id
    private String id;
    private String currency;
    private String description;
    private String displaySymbol;
    private String figi;
    private String isin;
    private String mic;
    private String shareClassFIGI;
    private String symbol;
    private String symbol2;
    private String type;
}
