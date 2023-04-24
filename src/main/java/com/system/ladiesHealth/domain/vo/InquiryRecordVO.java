package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "问诊记录")
public class InquiryRecordVO {

    /*
    1. 角色
    2. 内容
     */

    @Schema(description = "角色")
    private String role;

    @Schema(description = "内容")
    private String message;

}
