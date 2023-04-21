package com.system.ladiesHealth.controller;


import com.system.ladiesHealth.domain.dto.BasicSignDTO;
import com.system.ladiesHealth.domain.dto.MenstrualDTO;
import com.system.ladiesHealth.domain.vo.*;
import com.system.ladiesHealth.domain.vo.base.Res;
import com.system.ladiesHealth.service.PersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Past;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Tag(name = "【04】体征相关接口")
@RequestMapping("/personal")
@SecurityRequirement(name = "Bearer Authentication")
@Validated
public class PersonalController {

    @Autowired
    private PersonalService personalService;

    /*
    获取基本体征
     */
    @GetMapping("/basicSign")
    @Operation(summary = "获取基本体征")
    public Res<BasicSignVO> getBasicSign(Authentication authentication) {
        return personalService.getBasicSign(authentication.getName());
    }

    /*
    修改基本体征
     */
    @PostMapping("/basicSign")
    @Operation(summary = "修改基本体征")
    public Res<OperateVO> updateBasicSign(@Valid @RequestBody BasicSignDTO basicSignDTO, Authentication authentication) {
        return personalService.updateBasicSign(basicSignDTO, authentication.getName());
    }

    /*
    获取睡眠记录
     */
    @GetMapping("/sleepRecord")
    @Operation(summary = "获取睡眠记录")
    public Res<List<SleepRecordVO>> getSleepRecord(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date endTime,
            Authentication authentication) {
        return personalService.getSleepRecord(startTime, endTime, authentication.getName());
    }

    @PatchMapping("/sleepRecord")
    @Operation(summary = "新增睡眠记录")
    public Res<OperateVO> addSleepRecord(@RequestParam
                                         @DecimalMin(value = "0.5", message = "睡眠时长不能小于0.5小时")
                                         @DecimalMax(value = "24.0", message = "睡眠时长不能大于24小时")
                                         Double duration, Authentication authentication) {
        return personalService.addSleepRecord(duration, authentication.getName());
    }

    /*
    获取饮水记录
     */
    @GetMapping("/drinkRecord")
    @Operation(summary = "获取饮水记录")
    public Res<List<DrinkRecordVO>> getDrinkRecord(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date endTime,
            Authentication authentication) {
        return personalService.getDrinkRecord(startTime, endTime, authentication.getName());
    }

    @PatchMapping("/drinkRecord")
    @Operation(summary = "新增饮水记录")
    public Res<OperateVO> addDrinkRecord(@RequestParam
                                         @DecimalMin(value = "0.1", message = "饮水量不能小于0.1L")
                                         Double volume, Authentication authentication) {
        return personalService.addDrinkRecord(volume, authentication.getName());
    }

    /*
    获取用户曾经选过的运动类型
     */
    @GetMapping("/exerciseType")
    @Operation(summary = "获取用户曾经选过的运动类型")
    public Res<List<String>> getExerciseType(Authentication authentication) {
        return personalService.getExerciseType(authentication.getName());
    }

    /*
    获取运动记录
     */
    @GetMapping("/exerciseRecord")
    @Operation(summary = "获取运动记录")
    public Res<List<Map<String, String>>> getExerciseRecord(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") @Past Date endTime,
            Authentication authentication) {
        return personalService.getExerciseRecord(startTime, endTime, authentication.getName());
    }

    /*
    新增运动记录
     */
    @PatchMapping("/exerciseRecord")
    @Operation(summary = "新增运动记录")
    public Res<OperateVO> addExerciseRecord(@RequestParam String type,
                                            @RequestParam
                                            @DecimalMin(value = "0.5", message = "运动时长不能小于0.5小时")
                                            @DecimalMax(value = "24.0", message = "运动时长不能大于24小时")
                                            Double duration
    ) {
        return personalService.addExerciseRecord(type, duration);
    }

    /*
    新增月经记录
     */
    @PostMapping("/menstruationRecord")
    @Operation(summary = "新增月经记录")
    public Res<OperateVO> addMenstruationRecord(@Valid @RequestBody MenstrualDTO menstrualDTO, Authentication authentication) {
        return personalService.addMenstruationRecord(menstrualDTO, authentication.getName());
    }

    /*
    预测下一个月经周期
     */
    @GetMapping("/menstruationCycle/predict")
    @Operation(summary = "预测下一个月经周期")
    public Res<List<Date>> predictMenstruationCycle(Authentication authentication) {
        return personalService.predictMenstruationCycle(authentication.getName());
    }


    /*
    获取半年内月经报表相关信息
     */
    @GetMapping("/menstruationReport")
    @Operation(summary = "获取半年内月经报表相关信息")
    public Res<MenstruationVO> getMenstruationReport(Authentication authentication) {
        return personalService.getMenstruationReport(authentication.getName());
    }


}
