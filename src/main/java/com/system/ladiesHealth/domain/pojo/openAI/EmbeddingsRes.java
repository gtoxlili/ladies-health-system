package com.system.ladiesHealth.domain.pojo.openAI;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingsRes<T> {

    private Integer code;

    private String msg;

    private String response_time;

    private List<T> data;

}
