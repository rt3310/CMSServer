package com.malgn.cmsserver.member.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OnceAuthTokenServiceTest {

    OnceAuthTokenService onceAuthTokenService;

    @BeforeEach
    void setUp() {
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(30))
                .maximumSize(10000)
                .build();
        onceAuthTokenService = new OnceAuthTokenService(cache);
    }

    @Test
    @DisplayName("memberKey로 토큰을 생성한다.")
    void generateToken() {
        String memberKey = "memberKey";

        String token = onceAuthTokenService.generate(memberKey);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("memberKey가 null이면 토큰 생성 시 예외가 발생한다.")
    void throwExceptionWhenMemberKeyIsNull() {
        assertThatThrownBy(() -> onceAuthTokenService.generate(null))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MEMBER_KEY);
    }

    @Test
    @DisplayName("memberKey가 blank이면 토큰 생성 시 예외가 발생한다.")
    void throwExceptionWhenMemberKeyIsBlank() {
        assertThatThrownBy(() -> onceAuthTokenService.generate("   "))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MEMBER_KEY);
    }

    @Test
    @DisplayName("유효한 토큰으로 memberKey를 조회한다.")
    void validateAndConsumeValidToken() {
        String memberKey = "memberKey";
        String token = onceAuthTokenService.generate(memberKey);

        String result = onceAuthTokenService.validateAndConsume(token);

        assertThat(result).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("토큰이 null이면 예외가 발생한다.")
    void throwExceptionWhenTokenIsNull() {
        assertThatThrownBy(() -> onceAuthTokenService.validateAndConsume(null))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_ONCE_AUTH_TOKEN);
    }

    @Test
    @DisplayName("토큰이 blank이면 예외가 발생한다.")
    void throwExceptionWhenTokenIsBlank() {
        assertThatThrownBy(() -> onceAuthTokenService.validateAndConsume("   "))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_ONCE_AUTH_TOKEN);
    }

    @Test
    @DisplayName("존재하지 않는 토큰이면 예외가 발생한다.")
    void throwExceptionWhenTokenNotExists() {
        assertThatThrownBy(() -> onceAuthTokenService.validateAndConsume("notExistsToken"))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_ONCE_AUTH_TOKEN);
    }

    @Test
    @DisplayName("토큰은 일회성이므로 두 번 사용하면 예외가 발생한다.")
    void tokenCanBeUsedOnlyOnce() {
        String memberKey = "memberKey";
        String token = onceAuthTokenService.generate(memberKey);

        onceAuthTokenService.validateAndConsume(token);

        assertThatThrownBy(() -> onceAuthTokenService.validateAndConsume(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_ONCE_AUTH_TOKEN);
    }
}
