package com.system.ladiesHealth.domain.vo;


import com.system.ladiesHealth.constants.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户明细信息")
public class UserDetailVO {
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "角色")
    private RoleEnum role;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "电话")
    private String phone;
}
