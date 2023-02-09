
package com.system.ladiesHealth.configuration;

import com.system.ladiesHealth.constants.LoginTypeEnum;
import com.system.ladiesHealth.constants.RoleEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(RoleEnum.converter);
        registry.addConverter(LoginTypeEnum.converter);
    }
}
