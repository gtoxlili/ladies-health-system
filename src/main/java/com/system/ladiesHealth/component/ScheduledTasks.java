package com.system.ladiesHealth.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

    // 每 5 秒执行一次
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void reportCurrentTime() {
        log.info("线程：{}，时间：{}", Thread.currentThread().getName(), System.currentTimeMillis());
    }
}
