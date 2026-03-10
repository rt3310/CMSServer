package com.malgn.cmsserver.member.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class OnceAuthTokenService {

    private final Cache<String, String> tokenCache;

    public OnceAuthTokenService() {
        this.tokenCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(30))
                .maximumSize(10000)
                .build();
    }

    public String generate(String memberKey) {
        if (memberKey == null || memberKey.isBlank()) {
            throw new AppException(ErrorType.INVALID_MEMBER_KEY);
        }

        String token = UUID.randomUUID().toString();
        tokenCache.put(token, memberKey);
        return token;
    }

    public String validateAndConsume(String token) {
        if (token == null || token.isBlank()) {
            throw new AppException(ErrorType.INVALID_ONCE_AUTH_TOKEN);
        }

        String memberKey = tokenCache.asMap().remove(token);

        if (memberKey == null) {
            throw new AppException(ErrorType.INVALID_ONCE_AUTH_TOKEN);
        }

        return memberKey;
    }
}
