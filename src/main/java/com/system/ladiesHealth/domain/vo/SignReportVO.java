package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Schema(description = "问诊报告")
@EqualsAndHashCode(callSuper = true)
public class SignReportVO extends BasicSignVO {

    /*
    1. 近半年平均睡眠时长
    2. 近半年平均运动时长
    3. 近半年平均每日饮水量
    4. 近半年平均每日饮水次数
     */

    @Schema(description = "平均睡眠时长")
    private Double avgSleepTime;

    @Schema(description = "平均运动时长")
    private Double avgExerciseTime;

    @Schema(description = "平均每日饮水量")
    private Double avgDrinkWater;

    @Schema(description = "平均每日饮水次数")
    private Double avgDrinkTimes;

}
