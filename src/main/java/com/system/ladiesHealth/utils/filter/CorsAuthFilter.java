package com.system.ladiesHealth.utils.filter;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;


public class CorsAuthFilter {

    static public CorsFilter create(
            List<String> frontDomain,
            String tokenHeader
    ) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // 允许的域
        config.setAllowedOriginPatterns(frontDomain);

        // 允许的请求头
        config.setAllowedHeaders(
                Arrays.asList(
                        "Origin",
                        "Content-Type",
                        "Accept",
                        tokenHeader
                )
        );

        // max-age
        config.setMaxAge(300L);

        // 允许的响应头
        config.addExposedHeader("*");

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

}
