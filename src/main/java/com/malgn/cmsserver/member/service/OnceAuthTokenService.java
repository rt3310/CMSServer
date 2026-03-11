package com.malgn.cmsserver.member.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.malgn.cmsserver.support.exception.AppException;
import com.malgn.cmsserver.support.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnceAuthTokenService {

    private final Cache<String, String> onceAuthTokenCache;

    public String generate(String memberKey) {
        if (memberKey == null || memberKey.isBlank()) {
            throw new AppException(ErrorType.INVALID_MEMBER_KEY);
        }

        String token = UUID.randomUUID().toString();
        onceAuthTokenCache.put(token, memberKey);
        return token;
    }

    public String validateAndConsume(String token) {
        if (token == null || token.isBlank()) {
            throw new AppException(ErrorType.INVALID_ONCE_AUTH_TOKEN);
        }

        String memberKey = onceAuthTokenCache.asMap().remove(token);

        if (memberKey == null) {
            throw new AppException(ErrorType.INVALID_ONCE_AUTH_TOKEN);
        }

        return memberKey;
    }
}
