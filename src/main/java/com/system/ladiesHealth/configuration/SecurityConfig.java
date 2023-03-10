package com.system.ladiesHealth.configuration;


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
     * ?????????
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtUtil jwtUtil,
                                                   @Lazy AuthenticationManager authenticationManager
    ) throws Exception {
        return http
                // ?????? CSRF
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                // ??????????????????????????????, ???????????????
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/auth/register/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // ???????????????
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // ??????????????????
                .exceptionHandling()
                .authenticationEntryPoint(authorizedFailSupport)
                // .accessDeniedHandler(authorizedFailSupport)
                // ?????? ?????????????????? authenticated ???????????????????????????????????????????????????
                .and()
                // ???????????????
                .authenticationManager(authenticationManager)
                .addFilterBefore(new JwtAuthFilter(authenticationManager, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(CorsAuthFilter.create(frontDomain, tokenHeader)
                        , JwtAuthFilter.class)
                .build();

    }


}
