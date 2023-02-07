package com.system.ladiesHealth.utils.filter;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.jwt.JWTException;
import com.system.ladiesHealth.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthFilter extends BasicAuthenticationFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 是否是放行请求
        String token = request.getHeader(jwtUtil.getTokenHeader());
        try {
            Assert.isTrue(StrUtil.isNotBlank(token), jwtUtil.getTokenHeader() + " can not be empty");
            jwtUtil.verifyToken(token);
            Authentication authentication = jwtUtil.parseAuthentication(token);
            // 将认证信息存入 Spring 安全上下文中
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IllegalArgumentException | ValidateException | JWTException | JSONException e) {
            request.setAttribute("exceptionMessage", new BadCredentialsException(e.getLocalizedMessage()));
            SecurityContextHolder.clearContext();
        } catch (AccountExpiredException e) {
            request.setAttribute("exceptionMessage", e);
            SecurityContextHolder.clearContext();
        } finally {
            // 放行请求
            chain.doFilter(request, response);
        }
    }
}
