package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    JwtValidator jwtValidator;
    @Mock
    JwtGenerator jwtGenerator;
    @InjectMocks
    LoginService loginService;

    @Test
    @DisplayName("유효한 onceAuthToken으로 로그인하면 JWT가 반환된다.")
    void loginWithValidOnceAuthToken() {
        String onceAuthToken = "validOnceAuthToken";
        String memberKey = "memberKey";
        Jwt expectedJwt = new Jwt("accessToken", "refreshToken");

        given(jwtValidator.getSubjectIfValid(onceAuthToken)).willReturn(memberKey);
        given(jwtGenerator.generateJwt(memberKey)).willReturn(expectedJwt);

        Jwt jwt = loginService.login(onceAuthToken);

        assertThat(jwt.accessToken()).isEqualTo("accessToken");
        assertThat(jwt.refreshToken()).isEqualTo("refreshToken");
    }

    @Test
    @DisplayName("만료된 onceAuthToken으로 로그인하면 AppException이 발생한다.")
    void throwAppExceptionIfOnceAuthTokenIsExpired() {
        String expiredToken = "expiredToken";

        given(jwtValidator.getSubjectIfValid(expiredToken))
                .willThrow(new AppException(ErrorType.EXPIRED_JWT));

        assertThatThrownBy(() -> loginService.login(expiredToken))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.EXPIRED_JWT);
    }

    @Test
    @DisplayName("유효하지 않은 onceAuthToken으로 로그인하면 AppException이 발생한다.")
    void throwAppExceptionIfOnceAuthTokenIsInvalid() {
        String invalidToken = "invalidToken";

        given(jwtValidator.getSubjectIfValid(invalidToken))
                .willThrow(new AppException(ErrorType.MALFORMED_JWT));

        assertThatThrownBy(() -> loginService.login(invalidToken))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.MALFORMED_JWT);
    }

    @Test
    @DisplayName("서명이 유효하지 않은 onceAuthToken으로 로그인하면 AppException이 발생한다.")
    void throwAppExceptionIfSignatureIsInvalid() {
        String invalidSignatureToken = "invalidSignatureToken";

        given(jwtValidator.getSubjectIfValid(invalidSignatureToken))
                .willThrow(new AppException(ErrorType.INVALID_SIGNATURE));

        assertThatThrownBy(() -> loginService.login(invalidSignatureToken))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_SIGNATURE);
    }
}
