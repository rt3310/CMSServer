package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.Jwt;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtGenerator {

    private static final long ACCESS_TOKEN_VALIDATION_SECONDS = 60L * 30;
    private static final long REFRESH_TOKEN_VALIDATION_SECONDS = 60L * 60 * 24 * 14;

    private final SecretKey secretKey;

    public Jwt generateJwt(String memberKey) {
        if (memberKey == null || memberKey.isBlank()) {
            throw new AppException(ErrorType.INVALID_MEMBER_KEY);
        }
        return new Jwt(generateAccessToken(memberKey), generateRefreshToken(memberKey));
    }

    private String generateAccessToken(String memberKey) {
        return buildToken(memberKey, ACCESS_TOKEN_VALIDATION_SECONDS);
    }

    private String generateRefreshToken(String memberKey) {
        return buildToken(memberKey, REFRESH_TOKEN_VALIDATION_SECONDS);
    }

    private String buildToken(String memberKey, long expireSeconds) {
        return Jwts.builder()
                .subject(memberKey)
                .expiration(Date.from(Instant.now().plus(expireSeconds, ChronoUnit.SECONDS)))
                .signWith(secretKey)
                .compact();
    }
}
