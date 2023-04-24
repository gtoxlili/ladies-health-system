
package com.system.ladiesHealth.configuration;

import com.system.ladiesHealth.constants.LoginTypeEnum;
import com.system.ladiesHealth.constants.RoleEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${front.domain}")
    private String frontDomain;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(RoleEnum.converter);
        registry.addConverter(LoginTypeEnum.converter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(frontDomain)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Origin", "Content-Type", "Accept", tokenHeader)
                .allowCredentials(true)
                .maxAge(300L);
    }

}
