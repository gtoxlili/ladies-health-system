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
    private Integer created;
    private String model;
    private Choice[] choices;
    private Usage usage;

    @Data
    @AllArgsConstructor
    public static class Choice {
        private String text;
        private Integer index;
        private Object logprobs;
        private String finish_reason;
    }

    @Data
    @AllArgsConstructor
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }

}
