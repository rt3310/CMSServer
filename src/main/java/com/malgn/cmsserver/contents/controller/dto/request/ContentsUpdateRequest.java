package com.malgn.cmsserver.contents.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ContentsUpdateRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,
        String description
) {
}
