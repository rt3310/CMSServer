package com.malgn.cmsserver.member.service;

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

    SecretKey secretKey = Keys.hmacShaKeyFor("this_is_a_test_secret_key_abcdefghijtlmnopqr".getBytes(StandardCharsets.UTF_8));
    JwtValidator jwtValidator = new JwtValidator(secretKey);


    @Test
    @DisplayName("JWT 검증에 성공하면 subject를 반환한다.")
    void getMemberKeyIfJwtIsInvalid() {
        String memberKey = "memberKey";
        String token =
                Jwts.builder()
                        .subject(memberKey)
                        .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                        .signWith(secretKey)
                        .compact();

        String sub = jwtValidator.getSubjectIfValid(token);

        assertThat(sub).isEqualTo(memberKey);
    }

    @Test
    @DisplayName("토큰이 만료되면 AppException이 발생한다.")
    void throwAppExceptionExceptionIfTokenIsExpired() {
        String memberKey = "memberKey";
        String token =
                Jwts.builder()
                        .subject(memberKey)
                        .expiration(Date.from(Instant.now().minusMillis(1L)))
                        .signWith(secretKey)
                        .compact();

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValid(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.EXPIRED_JWT);
    }

    @Test
    @DisplayName("JWT가 손상되면 AppException이 발생한다.")
    void throwAppExceptionIfTokenIsMalformed() {
        String memberKey = "memberKey";
        String token =
                Jwts.builder()
                        .subject(memberKey)
                        .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                        .signWith(secretKey)
                        .compact()
                        .substring(1);

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
        String token =
                Jwts.builder()
                        .subject(memberKey)
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
    void throwAppExceptionExceptionIfJwtIsUnsupported() {
        String memberKey = "memberKey";
        String token =
                Jwts.builder()
                        .subject(memberKey)
                        .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                        .compact();

        assertThatThrownBy(() -> jwtValidator.validate(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.UNSUPPORTED_JWT);
    }

    @Test
    @DisplayName("JWT 검증에 성공해도 멤버 Key가 JWT에 들어있지 않으면 AppException이 발생한다.")
    void throwAppExceptionIfNotExistsMemberKeyInJwt() {
        String token =
                Jwts.builder()
                        .expiration(Date.from(Instant.now().plusSeconds(60 * 60)))
                        .signWith(secretKey)
                        .compact();

        assertThatThrownBy(() -> jwtValidator.getSubjectIfValid(token))
                .isInstanceOf(AppException.class)
                .extracting("errorType")
                .isEqualTo(ErrorType.NOT_FOUND_SUBJECT);
    }
}