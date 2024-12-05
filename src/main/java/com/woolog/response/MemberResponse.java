package com.woolog.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long id;

    @Builder
    public MemberResponse(Long id) {
        this.id = id;
    }
}
