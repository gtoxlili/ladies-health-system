package com.system.ladiesHealth.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class AuditorConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return Objects.isNull(authentication) || authentication.getPrincipal().equals("anonymousUser")
                    ? Optional.empty() :
                    Optional.of(authentication.getName());
        };
    }

}
