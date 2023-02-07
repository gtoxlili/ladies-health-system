package com.system.ladiesHealth.domain.vo.base;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.system.ladiesHealth.constants.ErrorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "通用返回结构体")
@AllArgsConstructor
public class Res<T> {
    @Schema(description = "状态码")
    private Integer code;

    @Schema(description = "错误描述")
    private String message;

    @Schema(description = "数据")
    private T data;

    public void setCode(ErrorStatus status) {
        this.code = status.getStatusCode();
    }

    static public <T> Res<T> ok(T data) {
        return new Res<>(200, null, data);
    }

    static public <T> Res<T> ok() {
        return new Res<>(200, null, null);
    }

    static public <T> Res<T> fail(ErrorStatus status, String message) {
        return new Res<>(status.getStatusCode(), message, null);
    }

    static public Res<String> fail(ErrorStatus status) {
        return new Res<>(status.getStatusCode(), status.getDefaultMessage(), null);
    }

    static public <T, R extends Throwable> Res<T> fail(ErrorStatus status, R e) {
        if (StrUtil.isBlank(e.getMessage())) {
            return new Res<>(status.getStatusCode(), status.getDefaultMessage(), null);
        }
        return new Res<>(status.getStatusCode(), e.getMessage(), null);
    }
}
