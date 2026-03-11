package com.malgn.cmsserver.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Value("${cache.once-auth-token.expire-seconds:30}")
    private long expireSeconds;

    @Value("${cache.once-auth-token.max-size:10000}")
    private long maxSize;

    @Bean
    public Cache<String, String> onceAuthTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(expireSeconds))
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }
}
