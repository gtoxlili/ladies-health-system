package com.system.ladiesHealth.configuration;


import cn.hutool.core.collection.ListUtil;
import com.system.ladiesHealth.exception.AuthorizedFailSupport;
import com.system.ladiesHealth.utils.JwtUtil;
import com.system.ladiesHealth.utils.filter.CorsAuthFilter;
import com.system.ladiesHealth.utils.filter.JwtAuthFilter;
import com.system.ladiesHealth.utils.filter.LogAuthFilter;
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
    private String[] frontDomain;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Autowired
    private LogAuthFilter logAuthFilter;

    @Bean
    public WebSecurityCustomizer ignoringCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html");
    }

    /**
     * 过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtUtil jwtUtil,
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
                .requestMatchers(HttpMethod.POST, "/auth/login/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/rollback/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/inquiry/completions/**").permitAll()
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
                .addFilterBefore(new JwtAuthFilter(authenticationManager, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(logAuthFilter, JwtAuthFilter.class)
                .addFilterBefore(CorsAuthFilter.create(ListUtil.toList(frontDomain), tokenHeader)
                        , LogAuthFilter.class)
                .build();

    }


}
