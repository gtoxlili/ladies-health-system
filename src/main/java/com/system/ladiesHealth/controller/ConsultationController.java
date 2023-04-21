package com.system.ladiesHealth.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "【03】问诊相关接口")
@RequestMapping("/consultation")
@SecurityRequirement(name = "Bearer Authentication")
public class ConsultationController {


}
