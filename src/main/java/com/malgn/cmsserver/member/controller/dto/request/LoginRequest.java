package com.malgn.cmsserver.member.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String onceAuthToken) {
}
