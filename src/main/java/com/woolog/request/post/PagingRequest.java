package com.woolog.request.post;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    private String keyword;

    public PagingRequest(int page, int size, String keyword) {
        this.page = page;
        this.size = size;
        this.keyword = keyword;
    }

    public long getOffset() {
        return ((long) max(this.page, 1) - 1) * min(this.size, MAX_SIZE);
    }

    public Pageable getPageable() {
        return PageRequest.of(this.page - 1, this.size);
    }
}
