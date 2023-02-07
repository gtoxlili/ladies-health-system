package com.system.ladiesHealth.controller;

import cn.hutool.core.util.StrUtil;
import com.system.ladiesHealth.constants.LoginTypeEnum;
import com.system.ladiesHealth.constants.RoleEnum;
import com.system.ladiesHealth.domain.dto.UserSubmitDTO;
import com.system.ladiesHealth.domain.vo.OperateVO;
import com.system.ladiesHealth.domain.vo.UserDetailVO;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.exception.BusinessException;
import com.system.ladiesHealth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "【02】认证授权")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login/{type}")
    @Operation(summary = "登录接口", description = "提供帐号密码，返回 Token, 以及用户信息")
    public ResponseEntity<Res<UserDetailVO>> login(
            @Validated({UserSubmitDTO.Login.class, UserSubmitDTO.Norm.class}) @RequestBody UserSubmitDTO userDTO,
            @Parameter(description = "登录类型", required = true) @PathVariable LoginTypeEnum type
    ) {
        if (type == LoginTypeEnum.ILLEGAL) {
            throw new BusinessException("Login type is illegal");
        }
        return authService.login(userDTO, type);
    }

    @GetMapping("/logout")
    @Operation(summary = "登出接口")
    public Res<Void> logout() {
        return authService.logout();
    }

    @PreAuthorize("#role.canRegister(authentication)")
    @PostMapping("/register/{role}")
    @Operation(summary = "注册接口", description = "注册")
    public ResponseEntity<Res<UserDetailVO>> register(
            @Validated({UserSubmitDTO.Register.class, UserSubmitDTO.Norm.class}) @RequestBody UserSubmitDTO userDTO,
            @Parameter(description = "角色", required = true) @PathVariable RoleEnum role) {
        return authService.register(userDTO, role);
    }

    @PutMapping("/register")
    @Operation(summary = "更新信息接口")
    public Res<OperateVO> update(
            @Validated(UserSubmitDTO.Norm.class) @RequestBody UserSubmitDTO userDTO,
            Authentication authentication) {
        // admin 可以修改任何用户信息
        // 其实存在一个问题，admin 可以修改其他 admin 的信息
        if (authentication.getAuthorities().stream().anyMatch(authority -> RoleEnum.valueOf(authority.getAuthority()).equals(RoleEnum.ROLE_ADMIN))) {
            if (StrUtil.isBlank(userDTO.getUsername())) {
                userDTO.setUsername(authentication.getName());
            }
            return authService.updateInfo(userDTO);
        }
        if (StrUtil.isNotBlank(userDTO.getUsername()) && !authentication.getName().equals(userDTO.getUsername())) {
            log.warn("用户 {} 在尝试修改用户 {} 的信息", authentication.getName(), userDTO.getUsername());
            throw new BusinessException("Unauthorized to modify other user information");
        }
        userDTO.setUsername(authentication.getName());
        return authService.updateInfo(userDTO);
    }

    @DeleteMapping("/register")
    @Operation(summary = "注销接口")
    public Res<OperateVO> delete(
            @Validated({UserSubmitDTO.Delete.class, UserSubmitDTO.Norm.class}) @RequestBody(required = false) List<UserSubmitDTO> userDTOs,
            Authentication authentication
    ) {
        if (userDTOs == null) {
            return authService.delete(authentication.getName());
        }
        return authService.delete(userDTOs.stream().map(UserSubmitDTO::getUsername).toList());
    }
}
