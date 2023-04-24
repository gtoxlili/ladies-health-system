package com.system.ladiesHealth.domain.pojo.openAI;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CompletionsReq {

    /*
    {
    "model": "gpt-3.5-turbo",
    "messages": [{"role": "user", "content": "Hello!"}]
  }
     */

    private String model;

    private List<Messages> messages;

    @Data
    @AllArgsConstructor
    public static class Messages {
        private String role;
        private String content;
    }

    private double temperature;

    private boolean stream;

    // 无主题
    public CompletionsReq(String messages) {
        this.model = "gpt-3.5-turbo";
        this.messages = List.of(new Messages("user", messages));
        this.temperature = 0.8;
        this.stream = false;
    }
}
