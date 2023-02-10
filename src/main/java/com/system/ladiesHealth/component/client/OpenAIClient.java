package com.system.ladiesHealth.component.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "OpenAI", url = "https://api.openai.com/v1")
public interface OpenAIClient {

    /*
    curl https://api.openai.com/v1/moderations \
      -X POST \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $OPENAI_API_KEY" \
      -d '{"input": "Sample text goes here"}'
    }'
     */
    @PostMapping(value = "/moderations", consumes = MediaType.APPLICATION_JSON_VALUE)
    String moderation(@RequestHeader("Authorization") String authorization, @RequestBody String body);

}
