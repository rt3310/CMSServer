package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtGeneratorTest {

    SecretKey secretKey = Keys.hmacShaKeyFor("this_is_a_test_secret_key_abcdefghijtlmnopqr".getBytes(StandardCharsets.UTF_8));
    JwtGenerator jwtGenerator = new JwtGenerator(secretKey);

    @Test
    @DisplayName("OnceAuthToken을 생성하면 subject에 memberKey가 포함된다.")
    void generateOnceAuthToken() {
        String memberKey = "memberKey";

        String token = jwtGenerator.generateOnceAuthToken(memberKey);

        Claims claims = parseToken(token);
        assertThat(claims.getSubject()).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("JWT를 생성하면 accessToken과 refreshToken이 반환된다.")
    void generateJwt() {
        String memberKey = "memberKey";

        Jwt jwt = jwtGenerator.generateJwt(memberKey);

        assertThat(jwt.accessToken()).isNotBlank();
        assertThat(jwt.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("JWT를 생성하면 accessToken의 subject에 memberKey가 포함된다.")
    void generateJwtAccessTokenContainsMemberKey() {
        String memberKey = "memberKey";

        Jwt jwt = jwtGenerator.generateJwt(memberKey);

        Claims claims = parseToken(jwt.accessToken());
        assertThat(claims.getSubject()).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("JWT를 생성하면 refreshToken의 subject에 memberKey가 포함된다.")
    void generateJwtRefreshTokenContainsMemberKey() {
        String memberKey = "memberKey";

        Jwt jwt = jwtGenerator.generateJwt(memberKey);

        Claims claims = parseToken(jwt.refreshToken());
        assertThat(claims.getSubject()).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("memberKey가 null이면 AppException이 발생한다.")
    void throwAppExceptionIfMemberKeyIsNull() {
        assertThatThrownBy(() -> jwtGenerator.generateJwt(null))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MEMBER_KEY);
    }

    @Test
    @DisplayName("memberKey가 빈 문자열이면 AppException이 발생한다.")
    void throwAppExceptionIfMemberKeyIsEmpty() {
        assertThatThrownBy(() -> jwtGenerator.generateJwt(""))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MEMBER_KEY);
    }

    @Test
    @DisplayName("memberKey가 공백 문자열이면 AppException이 발생한다.")
    void throwAppExceptionIfMemberKeyIsBlank() {
        assertThatThrownBy(() -> jwtGenerator.generateJwt("   "))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_MEMBER_KEY);
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
