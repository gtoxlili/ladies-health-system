package com.system.ladiesHealth.configuration.record;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Slf4j
@ConfigurationProperties(prefix = "jwt")
public record JwtRecord(
        String tokenHeader,
        String tokenPrefix,
        String alg,
        String secret,
        Integer refreshExpire
) {

    public JwtRecord {
        if (refreshExpire < 0) {
            log.error("refreshExpire must be greater than 0");
        }
    }

}
