package com.system.ladiesHealth.component.client;

import com.system.ladiesHealth.domain.pojo.openAI.*;
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
            value = "/completions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            headers = "Authorization=Bearer ${openai.api-key}"
    )
    CompletionsRes completion(@RequestBody CompletionsReq body);

    /*
    curl https://api.openai.com/v1/embeddings \
      -H "Authorization: Bearer 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" \
      -H "Content-Type: application/json" \
      -d '{
        "input": "The food was delicious and the waiter...",
        "model": "text-embedding-ada-002"
     }'
     */
    @PostMapping(
            value = "/embeddings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Authorization=Bearer ${openai.api-key}"
    )
    EmbeddingsRes embedding(@RequestBody EmbeddingsReq body);


}
