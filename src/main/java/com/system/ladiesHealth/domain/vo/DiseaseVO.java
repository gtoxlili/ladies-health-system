package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "相关疾病项响应")
public class DiseaseVO {

    /*
    1. 疾病名称
    2. 详情
     */

    @Schema(description = "疾病名称")
    private String name;

    @Schema(description = "疾病详情")
    private String detail;

}
