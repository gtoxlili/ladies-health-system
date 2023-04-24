package com.system.ladiesHealth.domain.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问诊主题")
public class InquiryTopicsVO {


    @Schema(description = "主题ID")
    private String topicId;

    @Schema(description = "主题标题")
    private String title;

}
