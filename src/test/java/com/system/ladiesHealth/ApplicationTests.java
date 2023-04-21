package com.system.ladiesHealth;

import com.system.ladiesHealth.component.client.OpenAIClient;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsReq;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private OpenAIClient openAIClient;

    @Test
    void contextLoads() {
        CompletionsReq completionsReq = CompletionsReq.defaultReq("The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n" +
                "\n" +
                "Human: Hello, who are you?\n" +
                "AI: I am an AI created by OpenAI. How can I help you today?\n" +
                "Human: 你好啊\n" +
                "AI:你好！有什么我能帮你的？\n" +
                "Human: 跟我说个故事\n" +
                "AI:");
        final CompletionsRes completion = openAIClient.completion(completionsReq);
        System.out.println(completion);
    }

}
