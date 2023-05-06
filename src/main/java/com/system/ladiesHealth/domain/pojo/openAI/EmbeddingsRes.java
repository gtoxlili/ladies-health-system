package com.system.ladiesHealth.domain.pojo.openAI;

import lombok.Data;

@Data
public class EmbeddingsRes<T> {

    private Integer code;

    private String msg;

    private String response_time;

    private T[] data;

}
