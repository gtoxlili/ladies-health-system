package com.system.ladiesHealth.domain.pojo.openAI;


import lombok.AllArgsConstructor;
import lombok.Data;

/*
'{
    "input": "The food was delicious and the waiter...",
    "model": "text-embedding-ada-002"
  }'
 */
@Data
@AllArgsConstructor
public class EmbeddingsReq<T> {

    private T text;

}
