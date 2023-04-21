package com.system.ladiesHealth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "基本体征")
public class BasicSignVO {

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "身高")
    private Integer height;

    @Schema(description = "体重")
    private Integer weight;

    @Schema(description = "血压")
    private String bloodPressure;

    @Schema(description = "心率")
    private Integer heartRate;

}
