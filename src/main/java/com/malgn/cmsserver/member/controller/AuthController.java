package com.malgn.cmsserver.member.controller;

import com.malgn.cmsserver.member.controller.dto.request.LoginRequest;
import com.malgn.cmsserver.member.controller.dto.response.JwtResponse;
import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.member.service.AuthService;
import com.malgn.cmsserver.support.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final AuthService authService;

    @NullMarked
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        Jwt jwt = authService.login(request.onceAuthToken());
        return ResponseEntity.ok(ApiResponse.success(new JwtResponse(jwt.accessToken(), jwt.refreshToken())));
    }

    @NullMarked
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(@RequestHeader(AUTHORIZATION) String authorization) {
        String refreshToken = extractToken(authorization);
        Jwt jwt = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(new JwtResponse(jwt.accessToken(), jwt.refreshToken())));
    }

    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith(BEARER)) {
            return authorization.substring(BEARER.length());
        }
        return authorization;
    }
}
