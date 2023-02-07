package com.system.ladiesHealth.exception;


import cn.hutool.json.JSONUtil;
import com.system.ladiesHealth.constants.ErrorStatus;
import com.system.ladiesHealth.domain.vo.base.Res;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.io.PrintWriter;


@Slf4j
@Component
public class AuthorizedFailSupport implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;


    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) {
        AuthenticationException exceptionMessage = (AuthenticationException) request.getAttribute("exceptionMessage");
        Res<Void> res = Res.fail(ErrorStatus.AUTHENTICATION_ERROR, exceptionMessage == null ? authException : exceptionMessage);
        if (exceptionMessage instanceof AccountExpiredException) {
            res.setCode(ErrorStatus.ACCOUNT_EXPIRED);
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JSONUtil.toJsonStr(res));
        } catch (IOException e) {
            log.error("response error :{}", e.getMessage());
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}
