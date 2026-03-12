package com.malgn.cmsserver.member.controller;

import com.malgn.cmsserver.member.controller.dto.request.LoginRequest;
import com.malgn.cmsserver.member.controller.dto.response.JwtResponse;
import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.member.service.AuthService;
import com.malgn.cmsserver.support.response.AppApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String AUTHORIZATION = "Authorization";

    private final AuthService authService;

    @Operation(summary = "로그인", description = "일회성 인증 토큰으로 JWT를 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 토큰")
    })
    @NullMarked
    @PostMapping("/login")
    public ResponseEntity<AppApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        Jwt jwt = authService.login(request.onceAuthToken());
        return ResponseEntity.ok(AppApiResponse.success(new JwtResponse(jwt.accessToken(), jwt.refreshToken())));
    }

    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 JWT를 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 토큰"),
            @ApiResponse(responseCode = "401", description = "토큰 만료")
    })
    @NullMarked
    @PostMapping("/refresh")
    public ResponseEntity<AppApiResponse<JwtResponse>> refreshToken(
            @Parameter(description = "Bearer {refreshToken}", required = true)
            @RequestHeader(AUTHORIZATION) String refreshToken) {
        Jwt jwt = authService.refresh(refreshToken);
        return ResponseEntity.ok(AppApiResponse.success(new JwtResponse(jwt.accessToken(), jwt.refreshToken())));
    }
}
