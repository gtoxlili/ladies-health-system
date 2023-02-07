package com.system.ladiesHealth.configuration;


import com.system.ladiesHealth.configuration.record.JwtRecord;
import com.system.ladiesHealth.exception.AuthorizedFailSupport;
import com.system.ladiesHealth.utils.JwtUtil;
import com.system.ladiesHealth.utils.filter.CorsAuthFilter;
import com.system.ladiesHealth.utils.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    private AuthorizedFailSupport authorizedFailSupport;

    @Value("${front.domain}")
    private String frontDomain;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .requestMatchers(HttpMethod.POST, "/auth/login/**")
                .requestMatchers(HttpMethod.GET, "/rollback/**");
    }

    /**
     * 过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtUtil jwtUtil,
                                                   JwtRecord jwtRecord,
                                                   @Lazy AuthenticationManager authenticationManager
    ) throws Exception {
        return http
                // 禁用 CSRF
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                // 指定哪些请求无需认证, 但走过滤器
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/auth/register/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // 不创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 处理异常情况
                .exceptionHandling()
                .authenticationEntryPoint(authorizedFailSupport)
                // .accessDeniedHandler(authorizedFailSupport)
                // 由于 全部路由都是 authenticated ，所以在此处不会出现越权访问的情况
                .and()
                // 添加过滤器
                .authenticationManager(authenticationManager)
                .addFilterBefore(new JwtAuthFilter(authenticationManager, jwtUtil, jwtRecord), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(CorsAuthFilter.create(frontDomain, tokenHeader)
                        , JwtAuthFilter.class)
                .build();

    }


}
