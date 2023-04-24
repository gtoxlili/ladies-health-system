package com.system.ladiesHealth.domain.pojo.openAI;


import lombok.AllArgsConstructor;
import lombok.Data;

/*
{
  "id": "cmpl-uqkvlQyYK7bGYrRHQ0eXlWi7",
  "object": "text_completion",
  "created": 1589478378,
  "model": "text-davinci-003",
  "choices": [
    {
      "text": "\n\nThis is indeed a test",
      "index": 0,
      "logprobs": null,
      "finish_reason": "length"
    }
  ],
  "usage": {
    "prompt_tokens": 5,
    "completion_tokens": 7,
    "total_tokens": 12
  }
}

 */

@Data
@AllArgsConstructor
public class CompletionsRes {

    private String id;
    private String object;
    private int created;
    private String model;
    private Usage usage;
    private Choices[] choices;

    @Data
    @AllArgsConstructor
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }

    @Data
    @AllArgsConstructor
    public static class Choices {
        private Message message;
        private String finish_reason;
        private int index;
        private Delta delta;

        @Data
        @AllArgsConstructor
        public static class Delta {
            private String content;
        }


        @Data
        @AllArgsConstructor
        public static class Message {
            private String role;
            private String content;
        }
    }

}
