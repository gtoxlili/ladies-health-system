package com.system.ladiesHealth.domain.pojo.openAI;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompletionsReq {

    /**
     * 模型名称
     */
    private String model;

    /**
     * 提示文本
     */
    private String prompt;

    /**
     * 最大生成的token数量
     */
    private Integer max_tokens;

    /**
     * 温度
     */
    private Double temperature;

    /**
     * top_p
     */
    private Double top_p;

    /**
     * 频率惩罚
     */
    private Double frequency_penalty;

    /**
     * 出现惩罚
     */
    private Double presence_penalty;

    /**
     * 是否流式
     */
    private Boolean stream;

    /**
     * 生成的数量
     */
    private Integer n;


    public static CompletionsReq defaultReq(String prompt) {
        return new CompletionsReq(
                "text-davinci-003",
                prompt,
                1536,
                0.7,
                1.0,
                0.0,
                0.0,
                false,
                1
        );
    }

}
