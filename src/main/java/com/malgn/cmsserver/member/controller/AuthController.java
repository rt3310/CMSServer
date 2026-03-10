package com.malgn.cmsserver.member.controller;

import com.malgn.cmsserver.member.controller.dto.request.LoginRequest;
import com.malgn.cmsserver.member.controller.dto.response.JwtResponse;
import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.member.service.LoginService;
import com.malgn.cmsserver.support.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @NullMarked
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        Jwt jwt = loginService.login(request.onceAuthToken());
        return ResponseEntity.ok(ApiResponse.success(new JwtResponse(jwt.accessToken(), jwt.refreshToken())));
    }
}
