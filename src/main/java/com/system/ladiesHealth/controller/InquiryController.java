package com.system.ladiesHealth.controller;


import com.system.ladiesHealth.domain.dto.InquiryDTO;
import com.system.ladiesHealth.domain.vo.InquiryRecordVO;
import com.system.ladiesHealth.domain.vo.InquiryTopicsVO;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.service.InquiryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "【03】问诊相关接口")
@RequestMapping("/inquiry")
@SecurityRequirement(name = "Bearer Authentication")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @PostMapping(value = {"register/", "register/{topicId}"})
    public Res<String> register(@RequestBody InquiryDTO inquiryDTO,
                                @PathVariable(required = false) String topicId,
                                Authentication authentication
    ) {
        if (topicId == null) {
            return inquiryService.registerInquiry(inquiryDTO.getMessage(), authentication.getName());
        } else {
            return inquiryService.continueInquiry(topicId, inquiryDTO.getMessage());
        }
    }

    /*
    根据 TopicID 获取问诊记录
     */
    @GetMapping(value = "records/{topicId}")
    public Res<List<InquiryRecordVO>> getInquiryRecord(@PathVariable String topicId) {
        return inquiryService.getInquiryRecords(topicId);
    }

    /*
    获取用户的问诊记录
     */
    @GetMapping(value = "topics")
    public Res<List<InquiryTopicsVO>> getInquiryTopics(Authentication authentication) {
        return inquiryService.getInquiryTopics(authentication.getName());
    }

    /*
    生成 Bot 回应
     */
    @GetMapping(value = "completions/{topicId}")
    public SseEmitter getCompletions(@PathVariable String topicId) {
        return inquiryService.getCompletions(topicId);
    }
}
