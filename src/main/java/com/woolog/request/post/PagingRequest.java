package com.woolog.request.post;

import lombok.Builder;
import lombok.Getter;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@Builder
public class PagingRequest {

    private static final int MAX_SIZE = 30;

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public PagingRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public long getOffset() {
        return ((long) max(this.page, 1) - 1) * min(this.size, MAX_SIZE);
    }
}
