package com.system.ladiesHealth.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "咨询请求")
public class InquiryDTO {

    @Schema(description = "咨询内容")
    private String message;

}
