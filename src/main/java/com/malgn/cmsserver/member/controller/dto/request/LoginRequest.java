package com.malgn.cmsserver.member.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "인증 토큰은 필수입니다.")
        String onceAuthToken
) {
}
