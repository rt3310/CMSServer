package com.malgn.cmsserver.member.service;

import com.malgn.cmsserver.member.domain.TokenType;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JwtValidator {

    private static final String BEARER = "Bearer ";

    private final SecretKey secretKey;

    public String getSubjectIfValidWithType(String token, TokenType expectedType) {
        if (!isBearerToken(token)) {
            throw new AppException(ErrorType.INVALID_TOKEN_METHOD);
        }

        String tokenBody = token.substring(BEARER.length());
        Claims claims = validate(tokenBody).getPayload();

        String tokenType = claims.get(JwtGenerator.TOKEN_TYPE_CLAIM, String.class);
        if (tokenType == null || !tokenType.equals(expectedType.name())) {
            throw new AppException(ErrorType.INVALID_TOKEN_TYPE);
        }

        String subject = claims.getSubject();
        if (subject == null) {
            throw new AppException(ErrorType.NOT_FOUND_SUBJECT);
        }

        return subject;
    }

    public Jws<Claims> validate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (MalformedJwtException e) {
            throw new AppException(ErrorType.MALFORMED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new AppException(ErrorType.UNSUPPORTED_JWT);
        } catch (ExpiredJwtException e) {
            throw new AppException(ErrorType.EXPIRED_JWT);
        } catch (SecurityException | io.jsonwebtoken.security.SignatureException e) {
            throw new AppException(ErrorType.INVALID_SIGNATURE);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorType.INVALID_JWT);
        }
    }

    private boolean isBearerToken(String token) {
        return token.startsWith(BEARER);
    }

}
