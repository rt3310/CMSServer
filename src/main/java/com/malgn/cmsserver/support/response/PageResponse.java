package com.malgn.cmsserver.support.response;

import org.springframework.data.domain.Slice;

import java.util.List;

public record PageResponse<T>(List<T> content, boolean hasNext) {

    public static <S> PageResponse<S> from(Slice<S> slice) {
        return new PageResponse<>(slice.getContent(), slice.hasNext());
    }
}
