package com.optimizely.javareactive.payloads;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> data;
    private long total;
    private int page;
    private int size;

}
