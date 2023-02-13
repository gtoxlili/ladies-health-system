package com.system.ladiesHealth;

import com.system.ladiesHealth.component.client.OpenAIClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private OpenAIClient openAIClient;

    @Test
    void contextLoads() {

    }

}
