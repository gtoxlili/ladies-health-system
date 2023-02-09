package com.system.ladiesHealth.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {

    @Value("${async.thread.core_pool_size}")
    private int corePoolSize;

    @Value("${async.thread.max_pool_size}")
    private int maxPoolSize;

    @Value("${async.thread.queue_capacity}")
    private int queueCapacity;

    @Value("${async.thread.name.prefix}")
    private String namePrefix;

    @Value("${async.thread.keep_alive_seconds}")
    private int keepAliveSeconds;

    @Value("${async.thread.rejection-policy}")
    private Class<? extends RejectedExecutionHandler> rejectionPolicy;

    @SneakyThrows
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        log.info("taskExecutor -------Initializing thread pool----------");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        log.info("taskExecutor corePoolSize: {}", corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        log.info("taskExecutor maxPoolSize: {}", maxPoolSize);
        // 任务队列大小
        executor.setQueueCapacity(queueCapacity);
        log.info("taskExecutor queueCapacity: {}", queueCapacity);
        // 线程前缀名
        executor.setThreadNamePrefix(namePrefix);
        log.info("taskExecutor namePrefix: {}", namePrefix.substring(0, namePrefix.length() - 1));
        // 线程的活跃时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        log.info("taskExecutor keepAliveSeconds: {}", keepAliveSeconds);
        // 拒绝策略
        executor.setRejectedExecutionHandler(rejectionPolicy.getDeclaredConstructor().newInstance());
        log.info("taskExecutor rejectionPolicy: {}", rejectionPolicy);
        // 线程初始化
        executor.initialize();
        log.info("taskExecutor -------Initializing thread pool completed----------");
        return executor;
    }

}
