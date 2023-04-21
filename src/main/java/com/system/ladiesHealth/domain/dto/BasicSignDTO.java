package com.system.ladiesHealth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "基本体征")
public class BasicSignDTO {

    @Schema(description = "年龄")
    @Max(value = 100, message = "年龄不能超过100岁")
    @Min(value = 1, message = "年龄不能小于1岁")
    private Integer age;

    @Schema(description = "身高")
    @Max(value = 250, message = "身高不能超过250cm")
    @Min(value = 50, message = "身高不能小于50cm")
    private Integer height;

    @Schema(description = "体重")
    private Integer weight;

    @Schema(description = "血压")
    @Pattern(regexp = "^\\d{2,3}/\\d{2,3}$", message = "血压格式错误")
    private String bloodPressure;

    @Schema(description = "心率")
    @Max(value = 200, message = "心率不能超过200次/分")
    @Min(value = 30, message = "心率不能小于30次/分")
    private Integer heartRate;

}
