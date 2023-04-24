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

    // 生成报表
    public String generateReport() {
        StringBuilder physicalCondition = new StringBuilder();
        if (this.getAge() != null) {
            physicalCondition.append("年龄：").append(this.getAge()).append("岁\n");
        }
        if (this.getHeight() != null) {
            physicalCondition.append("身高：").append(this.getHeight()).append("cm\n");
        }
        if (this.getWeight() != null) {
            physicalCondition.append("体重：").append(this.getWeight()).append("kg\n");
        }
        if (this.getBloodPressure() != null) {
            physicalCondition.append("血压：").append(this.getBloodPressure()).append("\n");
        }
        if (this.getHeartRate() != null) {
            physicalCondition.append("心率：").append(this.getHeartRate()).append("次/分钟\n");
        }
        if (this.getAvgSleepTime() != 0) {
            physicalCondition.append("平均睡眠时长：").append(String.format("%.2f", this.getAvgSleepTime())).append("小时\n");
        }
        if (this.getAvgExerciseTime() != 0) {
            physicalCondition.append("平均运动时长：").append(String.format("%.2f", this.getAvgExerciseTime())).append("小时\n");
        }
        if (this.getAvgDrinkWater() != 0) {
            physicalCondition.append("平均每日饮水量：").append(String.format("%.2f", this.getAvgDrinkWater())).append("ml\n");
        }
        if (this.getAvgDrinkTimes() != 0) {
            physicalCondition.append("平均每日饮水次数：").append(String.format("%.2f", this.getAvgDrinkTimes())).append("次\n");
        }
        return physicalCondition.toString();
    }

}
