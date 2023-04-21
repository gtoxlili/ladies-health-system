package com.system.ladiesHealth.utils.filter;

import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class LogAuthFilter extends OncePerRequestFilter {

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    protected void doFilterInternal(
            @Nullable HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable FilterChain chain) throws ServletException, IOException {
        if (request == null || response == null || chain == null) {
            return;
        }
        // 记录时刻
        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);

        log.info("[{}] {} from {}:{} - {} in {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getRemotePort(),
                response.getStatus(),
                formatTime(System.currentTimeMillis() - startTime));
    }

    private String formatTime(long time) {
        // time 单位为 毫秒
        if (time < 1000) {
            return time + "ms";
        } else {
            return time / 1000 + "s" + time % 1000 + "ms";
        }
    }

}
