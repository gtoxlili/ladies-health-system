package com.system.ladiesHealth.constants;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.system.ladiesHealth.dao.UserRepository;
import com.system.ladiesHealth.domain.dto.UserSubmitDTO;
import com.system.ladiesHealth.domain.po.UserPO;
import org.springframework.core.convert.converter.Converter;

import java.util.Optional;
import java.util.function.Function;

public enum LoginTypeEnum {

    @JsonProperty("username")
    USERNAME(UserSubmitDTO::getUsername, userRepository -> userRepository::findByUsername),

    @JsonProperty("email")
    EMAIL(UserSubmitDTO::getEmail, userRepository -> userRepository::findByEmail),

    @JsonProperty("phone")
    PHONE(UserSubmitDTO::getPhone, userRepository -> userRepository::findByPhone),

    @JsonProperty("illegal")
    ILLEGAL(null, null);

    // 取值方法
    private final Function<UserSubmitDTO, String> method;
    public final Function<UserRepository, Function<String, Optional<UserPO>>> findMethod;

    LoginTypeEnum(Function<UserSubmitDTO, String> method,
                  Function<UserRepository, Function<String, Optional<UserPO>>> findMethod) {
        this.method = method;
        this.findMethod = findMethod;
    }

    public String apply(UserSubmitDTO userDTO) {
        return method.apply(userDTO);
    }

    static public final Converter<String, LoginTypeEnum> converter = new Converter<>() {
        @Override
        public LoginTypeEnum convert(String source) {
            try {
                return LoginTypeEnum.valueOf(source.toUpperCase());
            } catch (Exception e) {
                return LoginTypeEnum.ILLEGAL;
            }
        }
    };
}
