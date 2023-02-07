package com.system.ladiesHealth.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.system.ladiesHealth.domain.pojo.RollbackPOJO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Value("${rollback.expire}")
    private int rollbackExpire;

    @Bean
    public Cache<String, RollbackPOJO> rollbackCache() {
        return Caffeine.newBuilder()
                .initialCapacity(1024)
                .expireAfterWrite(Duration.ofSeconds(rollbackExpire))
                .build();
    }

    @Bean
    public Cache<String, Authentication> authenticationCache() {
        return Caffeine.newBuilder()
                .initialCapacity(16)
                .maximumSize(128)
                .expireAfterWrite(Duration.ofDays(1))
                .build();
    }
}
