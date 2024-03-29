package com.system.ladiesHealth.utils.convert;

import com.system.ladiesHealth.domain.dto.BasicSignDTO;
import com.system.ladiesHealth.domain.dto.MenstrualDTO;
import com.system.ladiesHealth.domain.po.MenstrualRecordPO;
import com.system.ladiesHealth.domain.po.SignInfoPO;
import com.system.ladiesHealth.domain.vo.BasicSignVO;
import com.system.ladiesHealth.domain.vo.SignReportVO;
import org.mapstruct.*;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PersonalConvert {

    BasicSignVO generateBasicSignVOBySignInfoPO(SignInfoPO signInfoPO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSignInfoPOByBasicSignDTO(BasicSignDTO basicSignDTO, @MappingTarget SignInfoPO signInfoPO);

    // MenstrualDTO -> MenstrualRecordPO
    MenstrualRecordPO generateMenstrualRecordPOByMenstrualDTO(MenstrualDTO menstrualDTO);

    // BasicSignVO,avgSleepTime,avgSleepTime,avgDrinkWater,averageWaterTimes -> SignReportVO
    SignReportVO generateSignReportVO(
            BasicSignVO basicSignVO, Double avgSleepTime, Double avgExerciseTime, Double avgDrinkWater, Double avgDrinkTimes);

}
