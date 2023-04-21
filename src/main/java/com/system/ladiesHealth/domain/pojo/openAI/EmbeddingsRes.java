package com.system.ladiesHealth.domain.pojo.openAI;

import lombok.Data;

import java.util.List;

@Data
public class EmbeddingsRes {
    @Data
    public static class DataBean {
        private List<Double> embedding;
        private Integer index;
        private String object;
    }

    @Data
    public static class UsageBean {
        private Integer prompt_tokens;
        private Integer total_tokens;
    }

    private List<DataBean> data;
    private String model;
    private String object;
    private UsageBean usage;
}
