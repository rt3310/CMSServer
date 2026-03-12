package com.malgn.cmsserver.contents.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContentsUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String title,
        String description
) {
}
