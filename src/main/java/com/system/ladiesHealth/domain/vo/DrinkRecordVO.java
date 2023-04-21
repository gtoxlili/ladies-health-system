package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "睡眠记录")
public class DrinkRecordVO {

    @Schema(description = "记录时间")
    private String recordTime;

    @Schema(description = "饮水量")
    private Double drinkVolume;

    @Schema(description = "饮水次数")
    private Integer drinkTimes;

}
