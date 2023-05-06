package com.system.ladiesHealth.component.client;

import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsReq;
import com.system.ladiesHealth.domain.pojo.openAI.EmbeddingsRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "Microservices", url = "https://api.gtio.work")
public interface MicroservicesClient {
    @PostMapping(
            value = "/nlp/embedding?auth=${microservices.secret}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    EmbeddingsRes<Double> embedding(@RequestBody EmbeddingsReq<String> body);

    @PostMapping(
            value = "/nlp/embedding?auth=${microservices.secret}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    EmbeddingsRes<List<Double>> embeddingBulk(@RequestBody EmbeddingsReq<List<String>> body);
}
