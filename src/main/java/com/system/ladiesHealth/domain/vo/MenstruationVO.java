package com.system.ladiesHealth.domain.vo;

import cn.hutool.core.lang.Pair;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "半年内月经报表相关信息")
public class MenstruationVO {

    // 每月的月经天数
    @Schema(description = "每月的月经天数")
    private List<Pair<String, Long>> days;

    // 月经反应
    @Schema(description = "月经反应")
    private List<Pair<String, Long>> reactions;


}
