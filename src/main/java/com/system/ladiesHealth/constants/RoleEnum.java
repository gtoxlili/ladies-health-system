package com.system.ladiesHealth.constants;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public enum RoleEnum {
    @JsonProperty("admin")
    ROLE_ADMIN(3, "admin"),
    @JsonProperty("user")
    ROLE_USER(1, "user"),
    @JsonProperty("doctor")
    ROLE_DOCTOR(1, "doctor"),
    @JsonProperty("anonymous")
    ROLE_ANONYMOUS(2, "anonymous"),
    @JsonProperty("illegal")
    ROLE_ILLEGAL(99, "illegal");

    //  创建角色优先级
    //  ROLE_ADMIN >  ROLE_ANONYMOUS > ROLE_DOCTOR = ROLE_USER
    private final int createPriority;
    // 别名
    private final String alias;

    RoleEnum(int createPriority, String alias) {
        this.createPriority = createPriority;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    // 根据 createPriority 判断是否有权限创建
    public boolean canRegister(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            // 如果当前用户的权限优先于this，返回true
            if (this.createPriority <= RoleEnum.valueOf(authority.getAuthority()).createPriority) {
                return true;
            }
        }
        return false;
    }

    static public final Converter<String, RoleEnum> converter = new Converter<>() {
        @Override
        public RoleEnum convert(String source) {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                if (roleEnum.getAlias().equals(source)) {
                    return roleEnum;
                }
            }
            return ROLE_ILLEGAL;
        }
    };


}
