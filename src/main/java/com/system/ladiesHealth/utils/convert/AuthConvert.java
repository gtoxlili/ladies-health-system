package com.system.ladiesHealth.utils.convert;


import com.system.ladiesHealth.constants.RoleEnum;
import com.system.ladiesHealth.domain.dto.UserSubmitDTO;
import com.system.ladiesHealth.domain.po.UserPO;
import com.system.ladiesHealth.domain.vo.UserDetailVO;
import org.mapstruct.*;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AuthConvert {

    UserDetailVO generateUserDetailVOByUserPO(UserPO userPO);

    UserDetailVO generateUserDetailVOByUserSubmitDTO(UserSubmitDTO userSubmitDTO, RoleEnum role);

    UserPO generateUserPOByUserSubmitDTO(UserSubmitDTO userSubmitDTO, RoleEnum role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserPOByUserSubmitDTO(UserSubmitDTO userSubmitDTO, @MappingTarget UserPO userPO);
}
