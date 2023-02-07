package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "操作行为返回结构")
@Builder
@AllArgsConstructor
public class OperateVO {

    @Schema(description = "操作行为")
    private String action;

    @Schema(description = "回滚地址")
    private String rollbackUrl;

}
