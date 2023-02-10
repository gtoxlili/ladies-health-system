package com.system.ladiesHealth;

import com.system.ladiesHealth.component.client.OpenAIClient;
import feign.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private OpenAIClient openAIClient;


    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Test
    void contextLoads() {
        String body = "{\"input\": \"Sample text goes here\"}";
        String moderation = openAIClient.moderation(body);
        System.out.println(moderation);
    }

}
