package com.malgn.cmsserver.contents.controller.dto.response;

import com.malgn.cmsserver.contents.domain.Contents;

import java.time.LocalDateTime;

public record ContentsResponse(
        Long id,
        String title,
        String description,
        Long viewCount,
        LocalDateTime createdDate,
        String createdBy,
        LocalDateTime lastModifiedDate,
        String lastModifiedBy
) {

    public static ContentsResponse from(Contents contents) {
        return new ContentsResponse(
                contents.getId(),
                contents.getTitle(),
                contents.getDescription(),
                contents.getViewCount(),
                contents.getCreatedDate(),
                contents.getCreatedBy(),
                contents.getLastModifiedDate(),
                contents.getLastModifiedBy()
        );
    }
}
