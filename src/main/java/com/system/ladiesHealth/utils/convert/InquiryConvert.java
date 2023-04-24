package com.system.ladiesHealth.utils.convert;

import com.system.ladiesHealth.domain.po.InquiryRecordPO;
import com.system.ladiesHealth.domain.po.InquiryTopicsPO;
import com.system.ladiesHealth.domain.vo.InquiryRecordVO;
import com.system.ladiesHealth.domain.vo.InquiryTopicsVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InquiryConvert {

    List<InquiryRecordVO> generateInquiryRecordVOListByInquiryRecordPOList(List<InquiryRecordPO> inquiryRecordPOList);

    @Mapping(source = "id", target = "topicId")
    InquiryTopicsVO generateInquiryTopicsVOByInquiryTopicsPO(InquiryTopicsPO inquiryTopicsPO);

}
