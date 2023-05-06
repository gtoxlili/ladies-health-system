package com.system.ladiesHealth.component.client;

import com.system.ladiesHealth.domain.pojo.openAI.CompletionsReq;
import com.system.ladiesHealth.domain.pojo.openAI.CompletionsRes;
import com.system.ladiesHealth.domain.pojo.openAI.ModerationsReq;
import com.system.ladiesHealth.domain.pojo.openAI.ModerationsRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    @PostMapping(
            value = "/moderations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Authorization=Bearer ${openai.api-key}"
    )
    ModerationsRes moderation(@RequestBody ModerationsReq body);

    /*
    curl https://api.openai.com/v1/completions \
      -H 'Content-Type: application/json' \
      -H 'Authorization: Bearer YOUR_API_KEY' \
      -d '{
      "model": "text-davinci-003",
      "prompt": "Say this is a test",
      "max_tokens": 7,
      "temperature": 0
    }'
     */

    @PostMapping(
            value = "/chat/completions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Authorization=Bearer ${openai.api-key}"
    )
    CompletionsRes completion(@RequestBody CompletionsReq body);


}
