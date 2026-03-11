package com.malgn.cmsserver.contents.controller.dto.response;

import com.malgn.cmsserver.contents.domain.Contents;

import java.time.LocalDateTime;

public record ContentsListResponse(
        Long id,
        String title,
        Long viewCount,
        LocalDateTime createdDate,
        String createdBy
) {

    public static ContentsListResponse from(Contents contents) {
        return new ContentsListResponse(
                contents.getId(),
                contents.getTitle(),
                contents.getViewCount(),
                contents.getCreatedDate(),
                contents.getCreatedBy()
        );
    }
}
