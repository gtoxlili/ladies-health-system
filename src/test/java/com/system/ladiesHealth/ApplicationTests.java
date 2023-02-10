package com.system.ladiesHealth;

import com.system.ladiesHealth.component.client.OpenAIClient;
import feign.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private OpenAIClient openAIClient;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Test
    void contextLoads() {
        String body = "{\"input\": \"Sample text goes here\"}";
        String authorization = "Bearer " + openaiApiKey;
        String moderation = openAIClient.moderation(authorization, body);
        System.out.println(moderation);
    }

}
