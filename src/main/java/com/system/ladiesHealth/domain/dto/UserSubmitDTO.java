package com.system.ladiesHealth.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.system.ladiesHealth.utils.deserializer.EmptyToNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
// 空字符串作为 Null
@Schema(description = "用户提交信息请求")
public class UserSubmitDTO {


    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空", groups = {Register.class, Delete.class})
    @Size(min = 6, max = 20, message = "用户名长度必须在6-20之间", groups = {Norm.class})
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "用户名只能包含英文字母和数字", groups = {Norm.class})
    @JsonDeserialize(using = EmptyToNull.class)
    private String username;


    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空", groups = {Login.class, Register.class})
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "用户密码必须包含大小写字母和数字，且长度不小于8", groups = {Norm.class})
    @JsonDeserialize(using = EmptyToNull.class)
    //@Size(min = 60, max = 60, message = "密码格式错误") 等到前端加密后再加上
    private String password;


    @Schema(description = "邮箱")
    @Email(message = "邮箱格式错误", groups = {Norm.class})
    @JsonDeserialize(using = EmptyToNull.class)
    private String email;

    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误", groups = {Norm.class})
    @JsonDeserialize(using = EmptyToNull.class)
    private String phone;

    // 七天内免登录
    @Schema(description = "是否记住我")
    private Boolean rememberMe;

    public interface Register {
    }

    public interface Login {
    }

    public interface Delete {
    }

    public interface Norm {
    }

}
