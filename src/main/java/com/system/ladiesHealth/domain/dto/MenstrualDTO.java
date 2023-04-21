package com.system.ladiesHealth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Schema(description = "月经记录")
public class MenstrualDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "开始时间")
    @Past
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束时间")
    @Past
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    @Schema(description = "流量")
    @DecimalMin(value = "0.0", message = "流量不能小于0")
    @DecimalMax(value = "5.0", message = "流量不能大于5")
    @NotNull(message = "流量不能为空")
    private Double flow;

    @Schema(description = "疼痛")
    @DecimalMin(value = "0.0", message = "疼痛不能小于0")
    @DecimalMax(value = "5.0", message = "疼痛不能大于5")
    @NotNull(message = "疼痛不能为空")
    private Double pain;

    @Schema(description = "便秘")
    @NotNull(message = "便秘不能为空")
    private Boolean constipation;

    @Schema(description = "恶心")
    @NotNull(message = "恶心不能为空")
    private Boolean nausea;

    @Schema(description = "发冷")
    @NotNull(message = "发冷不能为空")
    private Boolean cold;

    @Schema(description = "膀胱失禁")
    @NotNull(message = "膀胱失禁不能为空")
    private Boolean incontinence;

    @Schema(description = "潮热")
    @NotNull(message = "潮热不能为空")
    private Boolean hot;

}
