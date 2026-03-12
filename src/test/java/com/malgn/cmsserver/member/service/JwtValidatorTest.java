package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.TokenType;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtValidatorTest {

    private static final String BEARER = "Bearer ";

    SecretKey secretKey = Keys.hmacShaKeyFor("this_is_a_test_secret_key_abcdefghijtlmnopqr".getBytes(StandardCharsets.UTF_8));
    JwtValidator jwtValidator = new JwtValidator(secretKey);

    @Test
    @DisplayName("ACCESS 토큰 검증에 성공하면 subject를 반환한다.")
    void getSubjectIfAccessTokenIsValid() {
        String memberKey = "memberKey";
        String token = createToken(memberKey, TokenType.ACCESS, 60 * 60);

        String subject = jwtValidator.getSubjectIfValidWithType(BEARER + token, TokenType.ACCESS);

        assertThat(subject).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("REFRESH 토큰 검증에 성공하면 subject를 반환한다.")
    void getSubjectIfRefreshTokenIsValid() {
        String memberKey = "memberKey";
        String token = createToken(memberKey, TokenType.REFRESH, 60 * 60);

        String subject = jwtValidator.getSubjectIfValidWithType(BEARER + token, TokenType.REFRESH);

        assertThat(subject).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("Bearer prefix가 없으면 AppException이 발생한다.")
    void throwAppExceptionIfBearerPrefixIsMissing() {
        String memberKey = "memberKey";
        String token = createToken(memberKey, TokenType.ACCESS, 60 * 60);

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValidWithType(token, TokenType.ACCESS))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_TOKEN_METHOD);
    }

    @Test
    @DisplayName("토큰 타입이 일치하지 않으면 AppException이 발생한다.")
    void throwAppExceptionIfTokenTypeMismatch() {
        String memberKey = "memberKey";
        String token = createToken(memberKey, TokenType.REFRESH, 60 * 60);

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValidWithType(BEARER + token, TokenType.ACCESS))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_TOKEN_TYPE);
    }

    @Test
    @DisplayName("토큰 타입 claim이 없으면 AppException이 발생한다.")
    void throwAppExceptionIfTokenTypeClaimIsMissing() {
        String memberKey = "memberKey";
        String token = Jwts.builder()
                .subject(memberKey)
                .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                .signWith(secretKey)
                .compact();

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValidWithType(BEARER + token, TokenType.ACCESS))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_TOKEN_TYPE);
    }

    @Test
    @DisplayName("토큰이 만료되면 AppException이 발생한다.")
    void throwAppExceptionIfTokenIsExpired() {
        String memberKey = "memberKey";
        String token = createToken(memberKey, TokenType.ACCESS, -1);

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValidWithType(BEARER + token, TokenType.ACCESS))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.EXPIRED_JWT);
    }

    @Test
    @DisplayName("JWT가 손상되면 AppException이 발생한다.")
    void throwAppExceptionIfTokenIsMalformed() {
        String memberKey = "memberKey";
        String token = createToken(memberKey, TokenType.ACCESS, 60 * 60).substring(1);

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.MALFORMED_JWT);
    }

    @Test
    @DisplayName("Signature가 맞지 않으면 AppException이 발생한다.")
    void throwAppExceptionIfSignatureIsInvalid() {
        String signature = "1signaturesignaturesignaturesignature";
        SecretKey invalidSecretKey = Keys.hmacShaKeyFor(signature.getBytes(StandardCharsets.UTF_8));
        String memberKey = "memberKey";
        String token = Jwts.builder()
                .subject(memberKey)
                .claim(JwtGenerator.TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                .signWith(invalidSecretKey)
                .compact();

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.INVALID_SIGNATURE);
    }

    @Test
    @DisplayName("지원하지 않는 JWT 형태이면 AppException이 발생한다.")
    void throwAppExceptionIfJwtIsUnsupported() {
        String memberKey = "memberKey";
        String token = Jwts.builder()
                .subject(memberKey)
                .claim(JwtGenerator.TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                .compact();

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.UNSUPPORTED_JWT);
    }

    @Test
    @DisplayName("subject가 없으면 AppException이 발생한다.")
    void throwAppExceptionIfSubjectIsMissing() {
        String token = Jwts.builder()
                .claim(JwtGenerator.TOKEN_TYPE_CLAIM, TokenType.ACCESS.name())
                .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                .signWith(secretKey)
                .compact();

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValidWithType(BEARER + token, TokenType.ACCESS))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.NOT_FOUND_SUBJECT);
    }

    private String createToken(String memberKey, TokenType tokenType, long expireSeconds) {
        return Jwts.builder()
                .subject(memberKey)
                .claim(JwtGenerator.TOKEN_TYPE_CLAIM, tokenType.name())
                .expiration(Date.from(Instant.now().plusSeconds(expireSeconds)))
                .signWith(secretKey)
                .compact();
    }
}
