package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "睡眠记录")
public class SleepRecordVO {

    @Schema(description = "记录时间")
    private String recordTime;

    @Schema(description = "睡眠时长")
    private Double sleepTime;

}
